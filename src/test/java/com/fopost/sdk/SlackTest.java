package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.SlackChannel;
import com.fopost.sdk.model.SlackIdentity;
import com.fopost.sdk.model.SlackMember;
import com.fopost.sdk.param.UpdateSlackIdentityParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class SlackTest {

    @Test
    void listChannelsMapsTheFlags() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"C1","name":"general","is_private":false,"is_member":true,"is_current":true},
                                 {"id":"C2","name":"ops","is_private":true,"is_member":true,"is_current":false}]}""");

        List<SlackChannel> channels = TestSupport.client(transport).accounts().listSlackChannels("a1");

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/slack/channels", transport.last().url());
        assertEquals(new SlackChannel("C1", "general", false, true, true), channels.get(0));
        assertTrue(channels.get(1).isPrivate());
    }

    @Test
    void listMembersKeepsNullNames() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"U1","name":"sam","real_name":"Sam Rivers","display_name":null,
                                  "avatar":null,"is_bot":false}]}""");

        List<SlackMember> members = TestSupport.client(transport).accounts().listSlackMembers("a1");

        assertEquals("https://api.fopost.test/v1/accounts/a1/slack/members", transport.last().url());
        SlackMember member = members.get(0);
        assertEquals("Sam Rivers", member.realName());
        assertNull(member.displayName());
        assertFalse(member.isBot());
    }

    @Test
    void identityGetAndPatchSendOnlyTheFieldsSet() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"username\":null,\"icon_url\":null,\"icon_emoji\":\":rocket:\"}}")
                .enqueue(200, "{\"data\":{\"username\":\"Release Bot\",\"icon_url\":null,\"icon_emoji\":null}}");
        FoPost client = TestSupport.client(transport);

        SlackIdentity got = client.accounts().getSlackIdentity("a1");
        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/slack/identity", transport.last().url());
        assertEquals(":rocket:", got.iconEmoji());

        SlackIdentity updated = client.accounts().updateSlackIdentity(
                "a1", UpdateSlackIdentityParams.create().username("Release Bot").iconEmoji(null));
        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/slack/identity", transport.last().url());
        assertEquals("{\"username\":\"Release Bot\",\"icon_emoji\":null}", transport.lastBody());
        assertEquals("Release Bot", updated.username());
    }

    @Test
    void webhookConnectionIsAConflict() {
        FakeTransport transport = new FakeTransport()
                .enqueue(409, "{\"error\":\"webhook_connection\",\"message\":\"Reconnect with the Slack app\"}");

        FoPostException error = assertThrows(FoPostException.class,
                () -> TestSupport.client(transport).accounts().listSlackChannels("a1"));

        assertEquals(409, error.status());
        assertEquals("webhook_connection", error.code());
    }
}
