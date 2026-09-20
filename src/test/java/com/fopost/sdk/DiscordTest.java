package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.DiscordChannel;
import com.fopost.sdk.model.DiscordMember;
import com.fopost.sdk.model.DiscordMessageRef;
import com.fopost.sdk.model.DiscordScheduledEvent;
import com.fopost.sdk.param.DiscordEventParams;
import com.fopost.sdk.param.UpdateDiscordIdentityParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class DiscordTest {

    @Test
    void listChannelsAndSwitchTheCurrentOne() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"c2","name":"launches","type":0,"parent_id":null,"nsfw":false,"can_post":true,"is_current":true}]}""")
                .enqueue(200, "{\"data\":{\"id\":\"c2\",\"name\":\"launches\",\"is_current\":true}}");
        FoPost client = TestSupport.client(transport);

        List<DiscordChannel> channels = client.accounts().listDiscordChannels("a1");
        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/discord/channels", transport.last().url());
        assertTrue(channels.get(0).isCurrent());

        client.accounts().switchDiscordChannel("a1", "c2");
        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/discord/channels/current", transport.last().url());
        assertEquals("{\"channel_id\":\"c2\"}", transport.lastBody());
    }

    @Test
    void identityPatchSendsOnlyTheFieldsSet() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"username\":\"Release Bot\",\"avatar_url\":null}}");

        TestSupport.client(transport)
                .accounts()
                .updateDiscordIdentity("a1", UpdateDiscordIdentityParams.create().username("Release Bot"));

        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/discord/identity", transport.last().url());
        assertEquals("{\"username\":\"Release Bot\"}", transport.lastBody());
    }

    @Test
    void scheduledEventRoundTrips() {
        String event = """
                {"data":{"id":"e1","name":"Launch stream","description":null,"channel_id":null,
                         "location":"https://example.com/live","start_time":"2026-10-01T18:00:00.000Z",
                         "end_time":"2026-10-01T19:00:00.000Z","status":"scheduled","user_count":0}}""";
        FakeTransport transport = new FakeTransport()
                .enqueue(201, event)
                .enqueue(200, "{\"data\":[" + event.substring(8, event.length() - 1) + "]}")
                .enqueue(200, "{\"data\":{\"id\":\"e1\",\"name\":\"Launch stream\",\"status\":\"canceled\"}}")
                .enqueue(200, "{\"data\":{\"deleted\":true}}");
        FoPost client = TestSupport.client(transport);

        DiscordScheduledEvent created = client.accounts().createDiscordEvent("a1", DiscordEventParams.create()
                .name("Launch stream")
                .startTime("2026-10-01T18:00:00.000Z")
                .endTime("2026-10-01T19:00:00.000Z")
                .location("https://example.com/live"));
        assertEquals("e1", created.id());
        assertEquals("https://api.fopost.test/v1/accounts/a1/discord/events", transport.last().url());
        assertEquals(
                "{\"name\":\"Launch stream\",\"start_time\":\"2026-10-01T18:00:00.000Z\","
                        + "\"end_time\":\"2026-10-01T19:00:00.000Z\",\"location\":\"https://example.com/live\"}",
                transport.lastBody());

        assertEquals(1, client.accounts().listDiscordEvents("a1").size());

        DiscordScheduledEvent updated = client.accounts()
                .updateDiscordEvent("a1", "e1", DiscordEventParams.create().status("canceled"));
        assertEquals("PATCH", transport.last().method());
        assertEquals("{\"status\":\"canceled\"}", transport.lastBody());
        assertEquals("canceled", updated.status());

        assertEquals(Boolean.TRUE, client.accounts().deleteDiscordEvent("a1", "e1").deleted());
        assertEquals("https://api.fopost.test/v1/accounts/a1/discord/events/e1", transport.last().url());
    }

    @Test
    void membersRolesAndDirectMessages() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"u7","username":"ada","display_name":null,"nick":null,"avatar":null,
                                  "is_bot":false,"roles":["r1"],"joined_at":null}]}""")
                .enqueue(200, "{\"data\":{\"assigned\":true}}")
                .enqueue(201, "{\"data\":{\"id\":\"m1\",\"channel_id\":\"dm1\"}}");
        FoPost client = TestSupport.client(transport);

        List<DiscordMember> members = client.accounts().listDiscordMembers("a1", "ada", null);
        assertEquals("https://api.fopost.test/v1/accounts/a1/discord/members?q=ada", transport.last().url());
        assertEquals(List.of("r1"), members.get(0).roles());

        assertEquals(Boolean.TRUE, client.accounts().addDiscordMemberRole("a1", "r1", "u7").assigned());
        assertEquals("PUT", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/discord/roles/r1/members/u7", transport.last().url());

        DiscordMessageRef sent = client.accounts().sendDiscordDm("a1", "u7", "hi");
        assertEquals("dm1", sent.channelId());
        assertEquals("{\"member_id\":\"u7\",\"content\":\"hi\"}", transport.lastBody());
    }

    @Test
    void webhookConnectionIsAConflict() {
        FakeTransport transport = new FakeTransport()
                .enqueue(409, "{\"error\":\"webhook_connection\",\"message\":\"Upgrade it to the bot first\"}");

        FoPostException error = assertThrows(FoPostException.class,
                () -> TestSupport.client(transport).accounts().listDiscordChannels("a1"));

        assertEquals(409, error.status());
        assertEquals("webhook_connection", error.code());
    }
}
