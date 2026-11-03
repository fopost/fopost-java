package com.fopost.sdk.param;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
     * for posts we were tagged in, {@code reviews} for one row per review left on the business.
     */
    public InboxThreadParams kind(String kind) {
        query.put("kind", kind);
        return this;
    }

    /** One or more connected accounts; sent comma-separated. */
    public InboxThreadParams accountId(String... accountIds) {
        return accountIds(accountIds == null ? null : Arrays.asList(accountIds));
    }

    public InboxThreadParams accountIds(List<String> accountIds) {
        List<String> ids = new ArrayList<>();
        if (accountIds != null) {
            for (String id : accountIds) {
                if (id != null && !id.isEmpty()) {
                    ids.add(id);
                }
            }
        }
        if (ids.isEmpty()) {
            query.remove("account_id");
        } else {
            query.put("account_id", String.join(",", ids));
        }
        return this;
    }

    /** {@code unanswered}, {@code replied} or {@code resolved}. A reply also resolves an item. */
    public InboxThreadParams status(String status) {
        query.put("status", status);
        return this;
    }

    /** Only items the platform dates at or after this instant. */
    public InboxThreadParams since(Instant since) {
        Params.put(query, "since", Params.iso(since));
        return this;
    }

    /** {@code newest} or {@code oldest}. */
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
