package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.Contact;
import com.fopost.sdk.model.ContactChannel;
import com.fopost.sdk.model.ContactConversation;
import com.fopost.sdk.model.ContactField;
import com.fopost.sdk.model.ContactImportResult;
import com.fopost.sdk.model.ContactPage;
import com.fopost.sdk.model.ConversationAnalytics;
import com.fopost.sdk.param.ContactParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class ContactsTest {

    private static final String CONTACT =
            """
            {"id":"con_1","display_name":"Ada Okafor",
             "channels":[{"platform":"instagram","handle":"adaokafor","externalId":"178414"},
                         {"platform":"x","handle":"ada_writes","externalId":null}],
             "source":"inbox","note":null,
             "first_seen_at":"2026-04-02T09:14:00.000Z","last_seen_at":"2026-09-18T14:30:00.000Z",
             "fields":{"plan_tier":"Pro"},
             "labels":[{"id":"lbl_1","name":"VIP","color":"#0070f3"}]}""";

    @Test
    void listReadsThePaginationBlockRatherThanMeta() {
        FakeTransport transport =
                new FakeTransport()
                        .enqueue(
                                200,
                                "{\"data\":[" + CONTACT + "],\"pagination\":{\"page\":2,\"per_page\":10,\"total\":11}}");

        ContactPage page =
                TestSupport.client(transport)
                        .contacts()
                        .list(ContactParams.Filter.create().workspace("w1").search("ada").page(2).perPage(10));

        assertEquals(
                "https://api.fopost.test/v1/contacts?workspace_id=w1&search=ada&page=2&per_page=10",
                transport.last().url());
        assertEquals(1, page.size());
        assertFalse(page.isEmpty());
        Contact contact = page.data().get(0);
        assertEquals("Ada Okafor", contact.displayName());
        assertEquals("178414", contact.channels().get(0).externalId());
        assertEquals("Pro", contact.fields().get("plan_tier"));
        assertEquals("VIP", contact.labels().get(0).name());
        assertEquals(11, page.pagination().total());
        assertEquals(2, page.pagination().page());
    }

    @Test
    void createSendsTheSnakeCaseWireNames() {
        FakeTransport transport = new FakeTransport().enqueue(201, "{\"data\":" + CONTACT + "}");

        TestSupport.client(transport)
                .contacts()
                .create(
                        ContactParams.Create.of("w1", List.of(ContactChannel.of("x", "ada_writes")))
                                .displayName("Ada Okafor")
                                .fields(java.util.Map.of("plan_tier", "Pro")));

        String body = transport.lastBody();
        assertTrue(body.contains("\"workspace_id\":\"w1\""), body);
        assertTrue(body.contains("\"display_name\":\"Ada Okafor\""), body);
        // An absent platform id must not travel as null: it would claim we know one.
        assertFalse(body.contains("external_id"), body);
    }

    @Test
    void updateClearsAFieldWithNullAndSendsNothingElse() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":" + CONTACT + "}");

        TestSupport.client(transport)
                .contacts()
                .update("con_1", ContactParams.Update.create().clearField("region"));

        assertEquals("PATCH", transport.last().method());
        assertEquals("{\"fields\":{\"region\":null}}", transport.lastBody());
    }

    @Test
    void conversationsReadsTheThreadsAContactAppearsIn() {
        FakeTransport transport =
                new FakeTransport()
                        .enqueue(
                                200,
                                """
                                {"data":[{"key":"t_182736","account_id":"acc_1","account_username":"yourbrand",
                                          "platform":"instagram","messages":14,"received":9,"sent":5,
                                          "last_message_at":"2026-09-18T14:30:00.000Z","last_item_id":"inb_1"}]}""");

        List<ContactConversation> rows =
                TestSupport.client(transport).contacts().conversations("con_1", 10);

        assertEquals(
                "https://api.fopost.test/v1/contacts/con_1/conversations?limit=10", transport.last().url());
        assertEquals(1, rows.size());
        assertEquals("t_182736", rows.get(0).key());
        assertEquals(9, rows.get(0).received());
    }

    @Test
    void importReportsWhatMergedAndWhatWasSkipped() {
        FakeTransport transport =
                new FakeTransport()
                        .enqueue(
                                200,
                                """
                                {"data":{"created":1,"merged":2,
                                         "skipped":[{"row":4,"reason":"platform and handle are both required"}],
                                         "unknownColumns":["lifetime_value"]}}""");

        ContactImportResult result =
                TestSupport.client(transport).contacts().importCsv("w1", "platform,handle\nx,ada_writes");

        assertEquals(1, result.created());
        assertEquals(2, result.merged());
        assertEquals(4, result.skipped().get(0).row());
        assertEquals(List.of("lifetime_value"), result.unknownColumns());
    }

    @Test
    void createFieldPutsTheWorkspaceOnTheQuery() {
        FakeTransport transport =
                new FakeTransport()
                        .enqueue(
                                201,
                                """
                                {"data":{"id":"fld_1","key":"plan_tier","name":"Plan Tier",
                                         "type":"select","options":["Free","Pro"],"position":0}}""");

        ContactField field =
                TestSupport.client(transport)
                        .contacts()
                        .createField("w1", "plan_tier", "Plan Tier", "select", List.of("Free", "Pro"));

        assertEquals(
                "https://api.fopost.test/v1/contacts/fields?workspace_id=w1", transport.last().url());
        assertEquals("plan_tier", field.key());
        assertEquals(List.of("Free", "Pro"), field.options());
    }

    @Test
    void conversationAnalyticsReadsTheAnalyticsRoute() {
        FakeTransport transport =
                new FakeTransport()
                        .enqueue(
                                200,
                                """
                                {"data":{"conversations":[{"key":"t_1","accountId":"acc_1","platform":"instagram",
                                          "received":9,"sent":5,"answered":5,"open":1,
                                          "medianResponseMinutes":47,"firstMessageAt":null,"lastMessageAt":null}],
                                         "total":128,"page":1,"perPage":25}}""");

        ConversationAnalytics report =
                TestSupport.client(transport)
                        .contacts()
                        .conversationAnalytics(ContactParams.Conversations.create().days(30).sort("slowest"));

        assertEquals(
                "https://api.fopost.test/v1/analytics/inbox/conversations?days=30&sort=slowest",
                transport.last().url());
        assertEquals(128, report.total());
        assertEquals(47.0, report.conversations().get(0).medianResponseMinutes());
    }
}
