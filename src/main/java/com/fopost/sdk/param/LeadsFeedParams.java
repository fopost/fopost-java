package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Filters for the leads feed. Every field is optional. */
public final class LeadsFeedParams {

    private final Map<String, Object> query = new LinkedHashMap<>();

    public static LeadsFeedParams create() {
        return new LeadsFeedParams();
    }

    public LeadsFeedParams workspaceId(String workspaceId) {
        query.put("workspace_id", workspaceId);
        return this;
    }

    public LeadsFeedParams formId(String formId) {
        query.put("form_id", formId);
        return this;
    }

    public LeadsFeedParams pageId(String pageId) {
        query.put("page_id", pageId);
        return this;
    }

    /** The {@code nextCursor} of the previous page. */
    public LeadsFeedParams cursor(String cursor) {
        query.put("cursor", cursor);
        return this;
    }

    /** 1 to 100. */
    public LeadsFeedParams limit(int limit) {
        query.put("limit", limit);
        return this;
    }

    public Map<String, Object> toQuery() {
        return new LinkedHashMap<>(query);
    }
}
