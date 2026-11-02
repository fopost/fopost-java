package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fopost.sdk.model.WebhookEvents;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WebhookVerifierTest {

    private static final String SECRET = "whsec_test";
    private static final long NOW = 1_760_000_000L;
    private static final Clock CLOCK = Clock.fixed(Instant.ofEpochSecond(NOW), ZoneOffset.UTC);

    private static final String BODY =
            """
            {"event":"post.published","timestamp":"2026-10-11T09:00:00.000Z",
             "data":{"id":"post_1","status":"published","title":"How to schedule posts"}}""";

    private static byte[] bytes(String body) {
        return body.getBytes(StandardCharsets.UTF_8);
    }

    private static Map<String, String> timestamped(String body, long timestamp) {
        return Map.of(
                "x-fopost-timestamp", Long.toString(timestamp),
                "x-fopost-signature-256", WebhookVerifier.sign(bytes(body), SECRET, timestamp),
                "x-fopost-signature", WebhookVerifier.sign(bytes(body), SECRET));
    }

    @Test
    void aFreshTimestampedSignaturePassesAndParsesTheEnvelope() {
        WebhookEnvelope envelope = WebhookVerifier.verify(bytes(BODY), timestamped(BODY, NOW - 10), SECRET, CLOCK);

        assertEquals(WebhookEvents.POST_PUBLISHED, envelope.event());
        assertEquals("2026-10-11T09:00:00.000Z", envelope.timestamp());
        assertEquals("post_1", envelope.data().path("id").asText());
    }

    @Test
    void aTamperedBodyFails() {
        String tampered = BODY.replace("How to schedule posts", "Something else");

        FoPostException error = assertThrows(
                FoPostException.class,
                () -> WebhookVerifier.verify(bytes(tampered), timestamped(BODY, NOW), SECRET, CLOCK));
        assertEquals("invalid_signature", error.code());
    }

    @Test
    void aStaleTimestampFailsEvenWhenTheSignatureMatches() {
        FoPostException error = assertThrows(
                FoPostException.class,
                () -> WebhookVerifier.verify(bytes(BODY), timestamped(BODY, NOW - 301), SECRET, CLOCK));
        assertEquals("stale_timestamp", error.code());

        WebhookVerifier.verify(bytes(BODY), timestamped(BODY, NOW - 300), SECRET, CLOCK);
    }

    @Test
    void theBodyOnlySignatureIsAcceptedWhenNoTimestampIsSent() {
        Map<String, String> legacy = Map.of("X-FoPost-Signature", WebhookVerifier.sign(bytes(BODY), SECRET));

        WebhookEnvelope envelope = WebhookVerifier.verify(BODY, legacy, SECRET);
        assertEquals(WebhookEvents.POST_PUBLISHED, envelope.event());

        FoPostException error = assertThrows(
                FoPostException.class,
                () -> WebhookVerifier.verify(bytes(BODY), Map.of("X-FoPost-Signature", "sha256=00"), SECRET));
        assertEquals("invalid_signature", error.code());
    }
}
