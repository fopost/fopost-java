package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Generated alt text: the path, the bare response, and an empty balance. */
class MediaAltTextTest {

    @Test
    void generateAltTextPostsToTheItemAndReadsTheText() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"alt_text\":\"A red bicycle against a white wall\",\"alt_text_source\":\"ai\","
                        + "\"tags\":[\"bicycle\",\"red\"]}");

        var alt = TestSupport.client(transport).media().generateAltText("med_1");

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/media/med_1/alt-text", transport.last().url());
        assertEquals("A red bicycle against a white wall", alt.altText());
        assertEquals("ai", alt.altTextSource());
        assertEquals(List.of("bicycle", "red"), alt.tags());
    }

    @Test
    void anEmptyBalanceIsPaymentRequired() {
        FakeTransport transport = new FakeTransport()
                .enqueue(402, "{\"error\":\"insufficient_credits\",\"message\":\"Your AI balance is empty.\","
                        + "\"upgrade_url\":\"/settings/billing\"}");

        var e = assertThrows(
                PaymentRequiredException.class,
                () -> TestSupport.client(transport).media().generateAltText("med_1"));

        assertEquals("insufficient_credits", e.code());
    }
}
