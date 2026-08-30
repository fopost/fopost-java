package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ErrorsTest {

    @Test
    void mapsEachStatusToItsOwnException() {
        assertThrows(AuthenticationException.class, () -> fail(401, "{\"error\":\"unauthorized\"}"));
        assertThrows(PaymentRequiredException.class, () -> fail(402, "{\"error\":\"insufficient_credits\"}"));
        assertThrows(PermissionDeniedException.class, () -> fail(403, "{\"error\":\"forbidden\"}"));
        assertThrows(NotFoundException.class, () -> fail(404, "{\"error\":\"not_found\"}"));
        assertThrows(ValidationException.class, () -> fail(422, "{\"error\":\"validation_error\"}"));
        assertThrows(FoPostException.class, () -> fail(500, "{\"error\":\"server_error\"}"));
    }

    @Test
    void carriesTheCodeAndMessageFromTheEnvelope() {
        FoPostException error = assertThrows(
                NotFoundException.class,
                () -> fail(404, "{\"error\":\"not_found\",\"message\":\"Post not found\"}"));

        assertEquals(404, error.status());
        assertEquals("not_found", error.code());
        assertEquals("Post not found", error.getMessage());
    }

    @Test
    void paymentRequiredExposesTheUpgradeUrl() {
        PaymentRequiredException error = assertThrows(
                PaymentRequiredException.class,
                () -> fail(402, "{\"error\":\"insufficient_credits\",\"upgrade_url\":\"https://fopost.com/billing\"}"));

        assertEquals("https://fopost.com/billing", error.upgradeUrl());
    }

    @Test
    void subscriptionRequiredIsRecognisable() {
        PermissionDeniedException error = assertThrows(
                PermissionDeniedException.class,
                () -> fail(403, "{\"error\":\"subscription_required\",\"message\":\"No active subscription\"}"));

        assertTrue(error.isSubscriptionRequired());
    }

    @Test
    void fallsBackToTheStatusWhenTheBodyIsNotJson() {
        FoPostException error = assertThrows(
                FoPostException.class,
                () -> {
                    FakeTransport transport = new FakeTransport()
                            .enqueue(502, "<html>bad gateway</html>", Map.of("content-type", "text/html"));
                    TestSupport.client(transport).workspaces().list();
                });

        assertEquals(502, error.status());
        assertTrue(error.getMessage().contains("bad gateway"));
    }

    private static void fail(int status, String body) {
        FakeTransport transport = new FakeTransport().enqueue(status, body);
        TestSupport.client(transport).workspaces().list();
    }
}
