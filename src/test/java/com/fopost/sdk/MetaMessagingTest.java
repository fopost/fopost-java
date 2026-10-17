package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.InboxHandover;
import com.fopost.sdk.model.MetaGreeting;
import com.fopost.sdk.model.MetaGreetingText;
import com.fopost.sdk.model.MetaIceBreaker;
import com.fopost.sdk.model.MetaIceBreakers;
import com.fopost.sdk.model.MetaMenuItem;
import com.fopost.sdk.model.MetaPersistentMenu;
import com.fopost.sdk.model.MetaPersistentMenuEntry;
import com.fopost.sdk.model.WebhookSubscription;
import java.util.List;
import org.junit.jupiter.api.Test;

class MetaMessagingTest {

    @Test
    void iceBreakersRoundTrip() {
        String body = "{\"data\":{\"ice_breakers\":[{\"question\":\"What are your hours?\",\"payload\":\"HOURS\"}]}}";
        FakeTransport transport = new FakeTransport()
                .enqueue(200, body)
                .enqueue(200, body)
                .enqueue(200, "{\"data\":{\"ice_breakers\":[]}}");
        FoPost client = TestSupport.client(transport);

        MetaIceBreakers got = client.accounts().getIceBreakers("a1");
        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/messaging/ice-breakers", transport.last().url());
        assertEquals("HOURS", got.iceBreakers().get(0).payload());

        MetaIceBreakers set = client.accounts()
                .setIceBreakers("a1", List.of(new MetaIceBreaker("What are your hours?", "HOURS")));
        assertEquals("PUT", transport.last().method());
        assertEquals(
                "{\"ice_breakers\":[{\"question\":\"What are your hours?\",\"payload\":\"HOURS\"}]}",
                transport.lastBody());
        assertEquals("What are your hours?", set.iceBreakers().get(0).question());

        MetaIceBreakers cleared = client.accounts().deleteIceBreakers("a1");
        assertEquals("DELETE", transport.last().method());
        assertTrue(cleared.iceBreakers().isEmpty());
    }

    @Test
    void aLinkMenuItemOmitsThePayloadKey() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"persistent_menu":[{"locale":"default","call_to_actions":[
                          {"type":"web_url","title":"Shop","url":"https://example.com/shop"}]}]}}""");

        MetaPersistentMenu set = TestSupport.client(transport).accounts().setPersistentMenu(
                "a1",
                List.of(MetaPersistentMenuEntry.defaultLocale(
                        List.of(MetaMenuItem.link("Shop", "https://example.com/shop")))));

        assertEquals("PUT", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/messaging/persistent-menu", transport.last().url());
        assertEquals(
                "{\"persistent_menu\":[{\"locale\":\"default\",\"call_to_actions\":"
                        + "[{\"type\":\"web_url\",\"title\":\"Shop\",\"url\":\"https://example.com/shop\"}]}]}",
                transport.lastBody());
        assertEquals("https://example.com/shop", set.persistentMenu().get(0).callToActions().get(0).url());
    }

    @Test
    void theGreetingDefaultsItsLocale() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"greeting\":[{\"locale\":\"default\",\"text\":\"Hi!\"}]}}");

        MetaGreeting saved = TestSupport.client(transport)
                .accounts()
                .setGreeting("a1", List.of(MetaGreetingText.of("Hi!")));

        assertEquals("{\"greeting\":[{\"locale\":\"default\",\"text\":\"Hi!\"}]}", transport.lastBody());
        assertEquals("default", saved.greeting().get(0).locale());
    }

    @Test
    void aLapsedSubscriptionIsReportedAndResubscribed() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"subscribed\":false,\"fields\":[\"feed\"],\"missing_fields\":[\"messages\"]}}")
                .enqueue(200,
                        "{\"data\":{\"subscribed\":true,\"fields\":[\"feed\",\"messages\"],\"missing_fields\":[]}}");
        FoPost client = TestSupport.client(transport);

        WebhookSubscription lapsed = client.accounts().getWebhookSubscription("a1");
        assertEquals("https://api.fopost.test/v1/accounts/a1/webhook-subscription", transport.last().url());
        assertFalse(lapsed.subscribed());
        assertEquals(List.of("messages"), lapsed.missingFields());

        WebhookSubscription fixed = client.accounts().resubscribeWebhook("a1");
        assertEquals("POST", transport.last().method());
        assertTrue(fixed.subscribed());
    }

    @Test
    void handoverPassesAndTakesControl() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"app_id\":\"263902037430900\",\"control\":\"passed\"}}")
                .enqueue(200, "{\"data\":{\"app_id\":null,\"control\":\"taken\"}}");
        FoPost client = TestSupport.client(transport);

        InboxHandover passed = client.inbox().passThreadControl("t_1", "a1", "263902037430900");
        assertEquals("https://api.fopost.test/v1/inbox/conversations/t_1/handover", transport.last().url());
        assertEquals("{\"account_id\":\"a1\",\"app_id\":\"263902037430900\"}", transport.lastBody());
        assertEquals("passed", passed.control());

        InboxHandover taken = client.inbox().takeThreadControl("t_1", "a1");
        assertEquals("{\"account_id\":\"a1\"}", transport.lastBody());
        assertNull(taken.appId());
        assertEquals("taken", taken.control());
    }
}
