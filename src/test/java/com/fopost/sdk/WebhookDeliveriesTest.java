package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class WebhookDeliveriesTest {

    private static final String DELIVERY =
            "{\"id\":\"d1\",\"webhook_id\":\"h1\",\"event\":\"post.published\",\"delivery_key\":\"k1\",\"attempt\":2,"
                    + "\"status\":\"failed\",\"response_status\":502,\"response_snippet\":\"Bad Gateway\",\"duration_ms\":140,"
                    + "\"payload_hash\":\"abc\",\"next_retry_at\":null,\"created_at\":\"2026-10-18T10:00:00Z\"}";

    @Test
    void deliveriesListSendsTheFilters() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":[" + DELIVERY + "]}");

        var deliveries = TestSupport.client(transport).webhooks().deliveries("h1", "failed", 10);

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/webhooks/h1/deliveries?status=failed&limit=10", transport.last().url());
        assertEquals("d1", deliveries.get(0).id());
        assertEquals(2, deliveries.get(0).attempt());
        assertEquals(502, deliveries.get(0).responseStatus());
        assertNull(deliveries.get(0).nextRetryAt());
    }

    @Test
    void oneDeliveryAndARedelivery() {
        FakeTransport transport = new FakeTransport().enqueue(200, DELIVERY).enqueue(204, "");
        FoPost client = TestSupport.client(transport);

        var delivery = client.webhooks().delivery("h1", "d1");
        assertEquals("https://api.fopost.test/v1/webhooks/h1/deliveries/d1", transport.last().url());
        assertEquals("k1", delivery.deliveryKey());

        client.webhooks().redeliver("h1", "d1");
        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/webhooks/h1/deliveries/d1/redeliver", transport.last().url());
    }
}
