package com.fopost.sdk.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The body of {@code POST /v1/ads/conversions}: offline events attributed to a
 * pixel the ad account owns. Emails and phone numbers are hashed by the API
 * before anything leaves FoPost.
 */
public final class UploadConversionsParams {

    private final Map<String, Object> body = new LinkedHashMap<>();
    private final List<Map<String, Object>> events = new ArrayList<>();

    private UploadConversionsParams() {}

    public static UploadConversionsParams of(
            String workspaceId, String connectionId, String adAccountId, String pixelId) {
        UploadConversionsParams params = new UploadConversionsParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("pixelId", pixelId);
        return params;
    }

    /** Up to 1000 events per call. {@code occurredAt} is ISO 8601. */
    public UploadConversionsParams event(String eventName, String occurredAt) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventName", eventName);
        event.put("occurredAt", occurredAt);
        events.add(event);
        return this;
    }

    /** Adds a field to the event added last. */
    public UploadConversionsParams with(String field, Object value) {
        if (events.isEmpty()) {
            throw new IllegalStateException("Add an event before its fields");
        }
        events.get(events.size() - 1).put(field, value);
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> out = new LinkedHashMap<>(body);
        out.put("events", new ArrayList<>(events));
        return out;
    }
}
