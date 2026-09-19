package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.InboxAccount;
import com.fopost.sdk.model.InboxConversationStarted;
import com.fopost.sdk.model.InboxItem;
import com.fopost.sdk.model.InboxPage;
import com.fopost.sdk.model.InboxRefreshResult;
import com.fopost.sdk.model.InboxReplyResult;
import com.fopost.sdk.param.InboxListParams;
import com.fopost.sdk.param.InboxReplyParams;
import com.fopost.sdk.param.StartConversationParams;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class InboxTest {

    @Test
    void listEncodesSnakeCaseFiltersAndReadsThePage() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"i1","workspaceId":"w1","platform":"instagram","type":"comment",
                                  "state":"unread","direction":"inbound","text":"Love this",
                                  "attachments":[],"platformCreatedAt":"2026-09-01T10:00:00.000Z",
                                  "canReply":true,"hidden":false,"canHide":true,"canDelete":false,
                                  "post":{"id":"p1","title":"Launch"},
                                  "account":{"id":"a1","platform":"instagram","username":"yourbrand","name":"Your Brand","avatar":null}}],
                         "meta":{"page":2,"perPage":10,"total":11}}""");

        InboxPage<InboxItem> page = TestSupport.client(transport)
                .inbox()
                .list(InboxListParams.create()
                        .workspaceId("w1")
                        .state("unread")
                        .postExternalId("ext-9")
                        .page(2)
                        .perPage(10));

        assertEquals("GET", transport.last().method());
        assertEquals(
                "https://api.fopost.test/v1/inbox?workspace_id=w1&state=unread&post_external_id=ext-9&page=2&per_page=10",
                transport.last().url());
        assertEquals(1, page.size());
        InboxItem item = page.data().get(0);
        assertEquals("comment", item.type());
        assertEquals(Instant.parse("2026-09-01T10:00:00Z"), item.platformCreatedAt());
        assertEquals("p1", item.post().id());
        assertEquals("yourbrand", item.account().username());
        assertEquals(11, page.meta().total());
        assertEquals(2, page.meta().page());
    }

    @Test
    void replyPostsTheTextAndUnwrapsTheResult() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"item":{"id":"i1","state":"resolved","repliedAt":"2026-09-01T11:00:00Z"},
                                 "reply":{"externalId":"c_42","externalUrl":"https://social.example/c/42"}}}""");

        InboxReplyResult result = TestSupport.client(transport).inbox().reply("i1", "Thanks!");

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/inbox/i1/reply", transport.last().url());
        assertEquals("{\"text\":\"Thanks!\"}", transport.lastBody());
        assertEquals("resolved", result.item().state());
        assertEquals("c_42", result.reply().externalId());
    }

    @Test
    void markThreadReadRefreshAndUpdateSendTheRightBodies() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"updated\":3}}")
                .enqueue(200, "{\"data\":{\"accountsPolled\":2,\"newItems\":5,\"rateLimited\":0,"
                        + "\"dmReconnect\":[{\"platform\":\"instagram\",\"account\":\"yourbrand\"}]}}")
                .enqueue(200, "{\"data\":{\"id\":\"i1\",\"state\":\"snoozed\",\"snoozedUntil\":\"2026-09-02T09:00:00Z\"}}")
                .enqueue(200, "{\"count\":4}");

        FoPost client = TestSupport.client(transport);

        assertEquals(3, client.inbox().markThreadRead("w1", "a1", "ext-9"));
        assertEquals(
                "{\"workspace_id\":\"w1\",\"account_id\":\"a1\",\"post_external_id\":\"ext-9\"}", transport.lastBody());

        InboxRefreshResult refresh = client.inbox().refresh("w1");
        assertEquals("{\"workspace_id\":\"w1\"}", transport.lastBody());
        assertEquals(5, refresh.newItems());
        assertEquals("yourbrand", refresh.dmReconnect().get(0).account());

        InboxItem snoozed = client.inbox().update("i1", "snoozed", Instant.parse("2026-09-02T09:00:00Z"));
        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/inbox/i1", transport.last().url());
        assertEquals("{\"state\":\"snoozed\",\"snoozedUntil\":\"2026-09-02T09:00:00Z\"}", transport.lastBody());
        assertEquals(Instant.parse("2026-09-02T09:00:00Z"), snoozed.snoozedUntil());

        assertEquals(4, client.inbox().unreadCount("w1"));
        assertTrue(transport.last().url().endsWith("/v1/inbox/unread-count?workspace_id=w1"));
    }

    @Test
    void approvalsUseTheIntegerIdAndAnOptionalText() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[{\"id\":7,\"source\":\"agent\",\"reply\":\"On it\","
                        + "\"createdAt\":\"2026-09-01T10:00:00Z\",\"item\":{\"id\":\"i1\",\"platform\":\"x\"}}]}")
                .enqueue(200, "{\"data\":{\"id\":7,\"outcome\":\"sent\"}}")
                .enqueue(200, "{\"data\":{\"id\":8,\"outcome\":\"rejected\"}}");

        FoPost client = TestSupport.client(transport);

        var approvals = client.inbox().listApprovals("w1");
        assertEquals(7L, approvals.get(0).id());
        assertEquals("x", approvals.get(0).item().platform());

        assertEquals("sent", client.inbox().approveReply(7, "Edited reply").outcome());
        assertEquals("https://api.fopost.test/v1/inbox/approvals/7/approve", transport.last().url());
        assertEquals("{\"text\":\"Edited reply\"}", transport.lastBody());

        assertEquals("rejected", client.inbox().rejectReply(8).outcome());
        assertEquals("https://api.fopost.test/v1/inbox/approvals/8/reject", transport.last().url());
    }

    @Test
    void likeAndPinPostWithoutABodyAndReadTheNewFlags() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"id":"i1","liked":true,"pinned":false,"reaction":null,
                                 "editedAt":"2026-09-02T09:00:00Z","canLike":true,"canPin":true,
                                 "canEdit":true,"canReact":false,"canSendMedia":false,
                                 "canQuickReply":false,"canPrivateReply":true}}""")
                .enqueue(200, "{\"data\":{\"id\":\"i1\",\"pinned\":true}}");

        FoPost client = TestSupport.client(transport);

        InboxItem liked = client.inbox().like("i1");
        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/inbox/i1/like", transport.last().url());
        assertTrue(liked.liked());
        assertFalse(liked.pinned());
        assertTrue(liked.canPrivateReply());
        assertFalse(liked.canSendMedia());
        assertEquals(Instant.parse("2026-09-02T09:00:00Z"), liked.editedAt());

        assertTrue(client.inbox().pin("i1").pinned());
        assertEquals("https://api.fopost.test/v1/inbox/i1/pin", transport.last().url());
    }

    @Test
    void reactSendsANullReactionToRemoveOurs() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"id\":\"i1\",\"reaction\":null}}");

        InboxItem item = TestSupport.client(transport).inbox().react("i1", null);

        assertEquals("https://api.fopost.test/v1/inbox/i1/react", transport.last().url());
        assertEquals("{\"reaction\":null}", transport.lastBody());
        assertNull(item.reaction());
    }

    @Test
    void editCommentPatchesOnlyTheText() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"id\":\"i1\",\"text\":\"Fixed the typo\"}}");

        InboxItem item = TestSupport.client(transport).inbox().editComment("i1", "Fixed the typo");

        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/inbox/i1", transport.last().url());
        assertEquals("{\"text\":\"Fixed the typo\"}", transport.lastBody());
        assertEquals("Fixed the typo", item.text());
    }

    @Test
    void replyWithParamsSendsSnakeCaseMediaAndQuickReplies() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"item\":{\"id\":\"i1\",\"state\":\"resolved\"},"
                        + "\"reply\":{\"externalId\":\"m_1\"}}}");

        InboxReplyResult result = TestSupport.client(transport)
                .inbox()
                .reply("i1", InboxReplyParams.create().mediaIds(List.of("med_1")).quickReplies(List.of("Yes", "No")));

        assertEquals("https://api.fopost.test/v1/inbox/i1/reply", transport.last().url());
        assertEquals("{\"media_ids\":[\"med_1\"],\"quick_replies\":[\"Yes\",\"No\"]}", transport.lastBody());
        assertEquals("m_1", result.reply().externalId());
    }

    @Test
    void startConversationAndTypingSendSnakeCaseBodies() {
        FakeTransport transport = new FakeTransport()
                .enqueue(201, "{\"data\":{\"conversationId\":\"conv_7\","
                        + "\"item\":{\"id\":\"i2\",\"direction\":\"outbound\"}}}")
                .enqueue(200, "{\"data\":{\"typing\":false}}")
                .enqueue(200, "{\"data\":[{\"id\":\"a1\",\"canStartConversation\":true}]}");

        FoPost client = TestSupport.client(transport);

        InboxConversationStarted started =
                client.inbox().startConversation(StartConversationParams.privateReply("i1", "Sent you the details"));
        assertEquals("https://api.fopost.test/v1/inbox/conversations", transport.last().url());
        assertEquals("{\"comment_id\":\"i1\",\"text\":\"Sent you the details\"}", transport.lastBody());
        assertEquals("conv_7", started.conversationId());
        assertEquals("i2", started.item().id());

        assertFalse(client.inbox().setTyping("conv_7", "a1", false));
        assertEquals("https://api.fopost.test/v1/inbox/conversations/conv_7/typing", transport.last().url());
        assertEquals("{\"account_id\":\"a1\",\"on\":false}", transport.lastBody());

        InboxAccount account = client.inbox().accounts().get(0);
        assertTrue(account.canStartConversation());
    }
}
