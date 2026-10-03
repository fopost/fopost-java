package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Filters for the activity log. Every field is optional; leaving {@code workspaceId} unset reads
 * every workspace the key can reach.
 */
public final class ActivityParams {

    /** The audit log: append-only, and the one kind that never expires. */
    public static final String KIND_SECURITY = "security";

    public static final String KIND_PUBLISH = "publish";
    public static final String KIND_CONNECTION = "connection";
    public static final String KIND_WEBHOOK = "webhook";
    public static final String KIND_INBOX = "inbox";
    public static final String KIND_AUTOMATION = "automation";
    public static final String KIND_BILLING = "billing";

    private String workspaceId;
    private String kind;
    private String from;
    private String to;
    private String cursor;
    private Integer limit;

    public static ActivityParams create() {
        return new ActivityParams();
    }

    public ActivityParams workspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
        return this;
    }

    /** One of the {@code KIND_*} constants. */
    public ActivityParams kind(String kind) {
        this.kind = kind;
        return this;
    }

    /** ISO 8601. Only events at or after this time. */
    public ActivityParams from(String from) {
        this.from = from;
        return this;
    }

    /** ISO 8601. Only events at or before this time. */
    public ActivityParams to(String to) {
        this.to = to;
        return this;
    }

    /** The {@code nextCursor} of the previous page. */
    public ActivityParams cursor(String cursor) {
        this.cursor = cursor;
        return this;
    }

    public ActivityParams limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Map<String, Object> toQuery() {
        Map<String, Object> query = new LinkedHashMap<>();
        Params.put(query, "workspace_id", workspaceId);
        Params.put(query, "kind", kind);
        Params.put(query, "from", from);
        Params.put(query, "to", to);
        Params.put(query, "cursor", cursor);
        Params.put(query, "limit", limit);
        return query;
    }
}
