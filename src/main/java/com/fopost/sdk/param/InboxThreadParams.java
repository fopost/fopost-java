package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Filters for listing comment threads and DM conversations. Every field is optional. */
public final class InboxThreadParams {

    private final Map<String, Object> query = new LinkedHashMap<>();

    public static InboxThreadParams create() {
        return new InboxThreadParams();
    }

    public InboxThreadParams workspaceId(String workspaceId) {
        query.put("workspace_id", workspaceId);
        return this;
    }

    /**
     * Threads only: {@code comments} (the default) for threads under our posts, {@code mentions}
     * for posts we were tagged in.
     */
    public InboxThreadParams kind(String kind) {
        query.put("kind", kind);
        return this;
    }

    public InboxThreadParams platform(String platform) {
        query.put("platform", platform);
        return this;
    }

    public InboxThreadParams accountId(String accountId) {
        query.put("account_id", accountId);
        return this;
    }

    /** {@code unread}, {@code read}, {@code resolved} or {@code snoozed}. */
    public InboxThreadParams state(String state) {
        query.put("state", state);
        return this;
    }

    public InboxThreadParams q(String q) {
        query.put("q", q);
        return this;
    }

    /** {@code newest}, {@code oldest} or {@code unanswered}. */
    public InboxThreadParams sort(String sort) {
        query.put("sort", sort);
        return this;
    }

    public InboxThreadParams page(int page) {
        query.put("page", page);
        return this;
    }

    public InboxThreadParams perPage(int perPage) {
        query.put("per_page", perPage);
        return this;
    }

    public Map<String, Object> toQuery() {
        return new LinkedHashMap<>(query);
    }
}
