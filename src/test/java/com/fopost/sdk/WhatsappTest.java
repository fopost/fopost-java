package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.Platforms;
import com.fopost.sdk.model.WhatsappSandboxSession;
import com.fopost.sdk.model.WhatsappTemplate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WhatsappTest {

    @Test
    void whatsappIsOnThePlatformList() {
        assertTrue(Platforms.ALL.contains(Platforms.WHATSAPP));
        assertEquals("whatsapp", Platforms.WHATSAPP);
    }

    @Test
    void createTemplateReturnsTheReviewStatusThePlatformGaveIt() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"id":"tpl-1","name":"order_shipped","language":"en_US","category":"UTILITY",
                                 "status":"PENDING","rejectedReason":null,"components":[],"qualityScore":null}}""");

        WhatsappTemplate template = TestSupport.client(transport)
                .whatsapp()
                .createTemplate(
                        "a1",
                        "order_shipped",
                        "en_US",
                        "UTILITY",
                        List.of(Map.of("type", "BODY", "text", "On its way.")));

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/whatsapp/templates", transport.last().url());
        // Nothing marks a template approved but the platform.
        assertEquals("PENDING", template.status());
        assertEquals("order_shipped", template.name());
    }

    @Test
    void deleteTemplateNamesItInTheQuery() {
        FakeTransport transport = new FakeTransport().enqueue(200, """
                {"data":{"deleted":true}}""");

        TestSupport.client(transport).whatsapp().deleteTemplate("a1", "tpl-1", "order_shipped");

        assertEquals("DELETE", transport.last().method());
        assertTrue(transport.last().url().endsWith("/whatsapp/templates/tpl-1?name=order_shipped"));
    }

    @Test
    void sandboxSessionCarriesOnlyTheLastFourDigits() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"id":"ses-1","status":"invited","phoneNumberLast4":"4567",
                                 "invitedAt":"2026-09-20T10:00:00Z","activatedAt":null,
                                 "expiresAt":"2026-09-21T10:00:00Z"}}""");

        WhatsappSandboxSession session =
                TestSupport.client(transport).whatsapp().createSandboxSession("ws", "+15551234567");

        assertEquals("https://api.fopost.test/v1/whatsapp/sandbox/sessions", transport.last().url());
        assertEquals("4567", session.phoneNumberLast4());
        assertEquals("invited", session.status());
    }
}
