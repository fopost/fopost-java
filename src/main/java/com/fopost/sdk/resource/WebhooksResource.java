package com.fopost.sdk.resource;

import com.fopost.sdk.WebhookEnvelope;
import com.fopost.sdk.WebhookVerifier;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Webhook;
import com.fopost.sdk.model.WebhookDelivery;
import com.fopost.sdk.param.UpdateWebhookParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Webhook endpoints, for publishing and account-health events. */
public final class WebhooksResource {

    private final ApiClient http;

    public WebhooksResource(ApiClient http) {
        this.http = http;
    }

    public List<Webhook> list() {
        return http.convertList(ApiClient.unwrap(http.get("/v1/webhooks", null)), Webhook.class);
    }

    /**
     * Register an endpoint. Events come from {@link com.fopost.sdk.model.WebhookEvents}.
     *
     * <p>The signing secret is on the returned object and is never shown again — store it now.
     */
    public Webhook create(String workspaceId, String url, List<String> events) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspaceId", workspaceId);
        body.put("url", url);
        body.put("events", events);
        return http.convert(ApiClient.unwrap(http.post("/v1/webhooks", body)), Webhook.class);
    }

    public Webhook update(String webhookId, UpdateWebhookParams params) {
        return http.convert(
                ApiClient.unwrap(http.put("/v1/webhooks/" + webhookId, params.toMap())), Webhook.class);
    }

    public void delete(String webhookId) {
        http.delete("/v1/webhooks/" + webhookId);
    }

    /** Send a sample event to the endpoint, to check it is reachable and verifies the signature. */
    public void test(String webhookId) {
        http.post("/v1/webhooks/" + webhookId + "/test", null);
    }

    /** Every delivery attempt to the endpoint, newest first. Attempts are kept for 30 days. */
    public List<WebhookDelivery> deliveries(String webhookId) {
        return deliveries(webhookId, null, null);
    }

    /** As {@link #deliveries(String)}, filtered to {@code success} or {@code failed} and capped at {@code limit}. */
    public List<WebhookDelivery> deliveries(String webhookId, String status, Integer limit) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (status != null) {
            query.put("status", status);
        }
        if (limit != null) {
            query.put("limit", limit);
        }
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/webhooks/" + webhookId + "/deliveries", query)),
                WebhookDelivery.class);
    }

    public WebhookDelivery delivery(String webhookId, String deliveryId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/webhooks/" + webhookId + "/deliveries/" + deliveryId, null)),
                WebhookDelivery.class);
    }

    /** Send the same event again, as a new attempt under the same delivery key. */
    public void redeliver(String webhookId, String deliveryId) {
        http.post("/v1/webhooks/" + webhookId + "/deliveries/" + deliveryId + "/redeliver", null);
    }

    /**
     * Check a delivery's signature and parse it. {@code rawBody} is the request body exactly as
     * received. See {@link WebhookVerifier} for the rule and the failure codes.
     */
    public WebhookEnvelope verify(byte[] rawBody, Map<String, String> headers, String secret) {
        return WebhookVerifier.verify(rawBody, headers, secret);
    }
}
