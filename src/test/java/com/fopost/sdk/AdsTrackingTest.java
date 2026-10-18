package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.AdPixelDetail;
import com.fopost.sdk.model.ConversionBatch;
import com.fopost.sdk.model.ConversionUploadResult;
import com.fopost.sdk.model.CustomConversion;
import com.fopost.sdk.model.EventMatchQuality;
import com.fopost.sdk.param.CreateCustomConversionParams;
import com.fopost.sdk.param.UploadConversionsParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdsTrackingTest {

    private static final String RESULT =
            "{\"data\":{\"id\":\"b1\",\"accepted\":1,\"eventCount\":1,\"replayed\":true}}";

    @Test
    void sendPixelConversionsCarriesTheIdempotencyKey() {
        FakeTransport transport = new FakeTransport().enqueue(202, RESULT);

        ConversionUploadResult result = TestSupport.client(transport)
                .ads()
                .sendPixelConversions(
                        UploadConversionsParams.of("w1", "c1", "act_1", "px_1")
                                .event("Purchase", "2026-10-18T10:04:00Z")
                                .with("eventId", "order-9")
                                .with("actionSource", "website")
                                .with("contentIds", List.of("sku_1"))
                                .with("valueMinor", 4999),
                        "key-9");

        assertTrue(result.replayed());
        assertEquals("b1", result.id());
        assertEquals("key-9", transport.last().headers().get("Idempotency-Key"));
        assertTrue(transport.last().url().endsWith("/v1/ads/conversions/events"));
        assertTrue(transport.lastBody().contains("\"contentIds\":[\"sku_1\"]"));
        assertTrue(transport.lastBody().contains("\"actionSource\":\"website\""));

        assertThrows(IllegalArgumentException.class, () -> TestSupport.client(transport)
                .ads()
                .sendPixelConversions(UploadConversionsParams.of("w1", "c1", "act_1", "px_1"), " "));
    }

    @Test
    void uploadConversionsSendsTheKeyOnlyWhenGiven() {
        FakeTransport transport = new FakeTransport().enqueue(202, RESULT).enqueue(202, RESULT);
        FoPost client = TestSupport.client(transport);
        UploadConversionsParams params =
                UploadConversionsParams.of("w1", "c1", "act_1", "px_1").event("Lead", "2026-10-18T10:04:00Z");

        client.ads().uploadConversions(params);
        assertNull(transport.last().headers().get("Idempotency-Key"));

        client.ads().uploadConversions(params, "key-1");
        assertEquals("key-1", transport.last().headers().get("Idempotency-Key"));
        assertTrue(transport.last().url().endsWith("/v1/ads/conversions"));
    }

    @Test
    void conversionBatchesListWithOptionalFilters() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[{\"id\":\"b1\",\"workspaceId\":\"w1\",\"connectionId\":\"c1\","
                        + "\"adAccountId\":\"act_1\",\"pixelId\":\"px_1\",\"idempotencyKey\":null,"
                        + "\"eventCount\":1,\"accepted\":1,\"events\":[{\"eventName\":\"Purchase\","
                        + "\"occurredAt\":\"2026-10-18T10:04:00Z\",\"eventId\":null,\"actionSource\":\"website\","
                        + "\"emailHash\":\"ab\",\"phoneHash\":null,\"externalIdHash\":null,\"valueMinor\":4999,"
                        + "\"currency\":\"USD\",\"orderId\":null}],\"createdAt\":\"2026-10-18T10:05:00Z\"}]}");

        List<ConversionBatch> batches = TestSupport.client(transport).ads().listConversionBatches("w1", null, 5);

        assertEquals("ab", batches.get(0).events().get(0).emailHash());
        assertNull(batches.get(0).idempotencyKey());
        String url = transport.last().url();
        assertTrue(url.contains("/v1/ads/conversions/events?"));
        assertTrue(url.contains("limit=5"));
        assertTrue(!url.contains("connection_id"));
    }

    @Test
    void pixelReadsMatchQualityAndCustomConversionCreate() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"id\":\"px_1\",\"name\":\"Site\",\"code\":\"<script></script>\","
                        + "\"createdAt\":null,\"lastFiredAt\":\"2026-10-17T00:00:00Z\",\"isUnavailable\":false,"
                        + "\"dataUseSetting\":\"analytics_only\"}}")
                .enqueue(200, "{\"data\":[{\"eventName\":\"Purchase\",\"score\":7.5,"
                        + "\"matchKeys\":[{\"identifier\":\"email\",\"coveragePercent\":80}]}]}")
                .enqueue(201, "{\"data\":{\"id\":\"cc_1\",\"name\":\"Signup\",\"pixelId\":\"px_1\","
                        + "\"eventType\":\"LEAD\",\"rule\":{\"eventName\":\"PageView\",\"urlContains\":\"/thanks\"},"
                        + "\"description\":null,\"defaultValueMinor\":500,\"isArchived\":false,"
                        + "\"isUnavailable\":false,\"createdAt\":null,\"lastFiredAt\":null}}")
                .enqueue(204, "");
        FoPost client = TestSupport.client(transport);

        AdPixelDetail pixel = client.ads().pixel("px_1", "w1", "c1", "act_1");
        assertEquals("analytics_only", pixel.dataUseSetting());
        assertEquals("<script></script>", pixel.code());
        assertTrue(transport.last().url().contains("/v1/ads/pixels/px_1?"));
        assertTrue(transport.last().url().contains("ad_account_id=act_1"));

        List<EventMatchQuality> quality = client.ads().eventMatchQuality("px_1", "w1", "c1", "act_1");
        assertEquals(7.5, quality.get(0).score());
        assertEquals(80.0, quality.get(0).matchKeys().get(0).coveragePercent());
        assertTrue(transport.last().url().contains("/v1/ads/pixels/px_1/match-quality?"));

        CustomConversion created = client.ads()
                .createCustomConversion(CreateCustomConversionParams.of("w1", "c1", "act_1", "px_1", "Signup", "LEAD")
                        .urlContains("/thanks")
                        .defaultValueMinor(500));
        assertEquals("/thanks", created.rule().urlContains());
        assertEquals(500L, created.defaultValueMinor());
        assertTrue(transport.last().url().endsWith("/v1/ads/custom-conversions"));
        assertTrue(transport.lastBody().contains("\"rule\":{\"urlContains\":\"/thanks\"}"));
        assertTrue(transport.lastBody().contains("\"eventType\":\"LEAD\""));

        client.ads().deleteCustomConversion("cc_1", "w1", "c1", "act_1");
        assertEquals("DELETE", transport.last().method());
        assertTrue(transport.lastBody().contains("\"adAccountId\":\"act_1\""));
    }
}
