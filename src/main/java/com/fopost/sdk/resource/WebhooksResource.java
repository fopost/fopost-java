package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Webhook;
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
}
