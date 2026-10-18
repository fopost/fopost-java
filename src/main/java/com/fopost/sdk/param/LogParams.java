package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Filters for the request log. Every field is optional; leaving {@code workspaceId} unset reads
 * every workspace the key can reach plus its own account-level requests.
 */
public final class LogParams {

    private final Map<String, Object> query = new LinkedHashMap<>();

    public static LogParams create() {
        return new LogParams();
    }

    public LogParams workspaceId(String workspaceId) {
        query.put("workspace_id", workspaceId);
        return this;
    }

    /** How far back to read, 1 to 30. */
    public LogParams days(int days) {
        query.put("days", days);
        return this;
    }

    /** {@code success} or {@code failed}. */
    public LogParams status(String status) {
        query.put("status", status);
        return this;
    }

    public LogParams method(String method) {
        query.put("method", method);
        return this;
    }

    public LogParams statusCode(int statusCode) {
        query.put("status_code", statusCode);
        return this;
    }

    public LogParams platform(String platform) {
        query.put("platform", platform);
        return this;
    }

    /** Exact route pattern, such as {@code /v1/posts/{id}}. */
    public LogParams route(String route) {
        query.put("route", route);
        return this;
    }

    /** Substring match against path and message. */
    public LogParams q(String q) {
        query.put("q", q);
        return this;
    }

    public LogParams limit(int limit) {
        query.put("limit", limit);
        return this;
    }

    /** The {@code nextCursor} of the previous page: rows older than this id. */
    public LogParams before(String before) {
        query.put("before", before);
        return this;
    }

    public Map<String, Object> toQuery() {
        return new LinkedHashMap<>(query);
    }
}
