package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.QueueSlot;
import com.fopost.sdk.model.QueueSlotDeleted;
import com.fopost.sdk.model.QueueSlotUpdate;
import com.fopost.sdk.param.QueueSlotParams;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The posting queue: recurring weekly slots, and the times a queued post would get. A post joins
 * the queue with {@code schedule: queue} on create or update, see
 * {@link com.fopost.sdk.param.CreatePostParams#queue()}. Every call needs the {@code posts} scope.
 *
 * <pre>{@code
 * client.queue().createSlot(workspaceId, QueueSlotParams.create().weekday(1).time("09:30").timezone("UTC"));
 * Instant next = client.queue().next(workspaceId);
 * }</pre>
 */
public final class QueueResource {

    private final ApiClient http;

    public QueueResource(ApiClient http) {
        this.http = http;
    }

    /** The workspace's slots, ordered by weekday and time. */
    public List<QueueSlot> listSlots(String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("workspace_id", workspaceId);
        return http.convertList(ApiClient.unwrap(http.get("/v1/queue/slots", query)), QueueSlot.class);
    }

    public QueueSlot createSlot(String workspaceId, QueueSlotParams params) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspace_id", workspaceId);
        body.putAll(params.toMap());
        return http.convert(ApiClient.unwrap(http.post("/v1/queue/slots", body)), QueueSlot.class);
    }

    /** Change a slot. Queued posts sitting on it move with it, listed in {@code moved}. */
    public QueueSlotUpdate updateSlot(String slotId, QueueSlotParams params) {
        return http.convert(
                ApiClient.unwrap(http.put("/v1/queue/slots/" + slotId, params.toMap())), QueueSlotUpdate.class);
    }

    /** Remove a slot. Queued posts sitting on it move to the next free one, listed in {@code moved}. */
    public QueueSlotDeleted deleteSlot(String slotId) {
        return http.convert(ApiClient.unwrap(http.delete("/v1/queue/slots/" + slotId)), QueueSlotDeleted.class);
    }

    /** The time a post added to the queue now would get, or null when no slot applies. */
    public Instant next(String workspaceId) {
        return next(workspaceId, null);
    }

    /** As {@link #next(String)}, for one account's slots. */
    public Instant next(String workspaceId, String accountId) {
        JsonNode next = ApiClient.unwrap(http.get("/v1/queue/next", query(workspaceId, accountId, null))).path("next");
        return next.isMissingNode() || next.isNull() ? null : Instant.parse(next.asText());
    }

    /** The next free occurrences in order, a post-by-post preview of the queue. */
    public List<Instant> preview(String workspaceId, String accountId, Integer count) {
        JsonNode data = ApiClient.unwrap(http.get("/v1/queue/preview", query(workspaceId, accountId, count)));
        List<Instant> times = new ArrayList<>();
        for (JsonNode node : data) {
            times.add(Instant.parse(node.asText()));
        }
        return times;
    }

    private static Map<String, Object> query(String workspaceId, String accountId, Integer count) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("workspace_id", workspaceId);
        if (accountId != null) {
            query.put("account_id", accountId);
        }
        if (count != null) {
            query.put("count", count);
        }
        return query;
    }
}
