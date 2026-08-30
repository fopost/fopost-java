package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A partial update to a webhook. */
public final class UpdateWebhookParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    public static UpdateWebhookParams create() {
        return new UpdateWebhookParams();
    }

    public UpdateWebhookParams url(String url) {
        body.put("url", url);
        return this;
    }

    /** Values from {@link com.fopost.sdk.model.WebhookEvents}. Replaces the current list. */
    public UpdateWebhookParams events(List<String> events) {
        body.put("events", events);
        return this;
    }

    /** Pause or resume delivery without deleting the endpoint. */
    public UpdateWebhookParams active(boolean active) {
        body.put("active", active);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
