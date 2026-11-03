package com.fopost.sdk.param;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Filters for listing inbox items. Every field is optional. */
public final class InboxListParams {

    private final Map<String, Object> query = new LinkedHashMap<>();

    public static InboxListParams create() {
        return new InboxListParams();
    }

    /** Without one, the list spans every workspace the key can reach. */
    public InboxListParams workspaceId(String workspaceId) {
        query.put("workspace_id", workspaceId);
        return this;
    }

    /** {@code comment}, {@code mention}, {@code review} or {@code dm}. */
    public InboxListParams type(String type) {
        query.put("type", type);
        return this;
    }

    /** One or more connected accounts; sent comma-separated. */
    public InboxListParams accountId(String... accountIds) {
        return accountIds(accountIds == null ? null : Arrays.asList(accountIds));
    }

    public InboxListParams accountIds(List<String> accountIds) {
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
    public InboxListParams status(String status) {
        query.put("status", status);
        return this;
    }

    /** Only items the platform dates at or after this instant. */
    public InboxListParams since(Instant since) {
        Params.put(query, "since", Params.iso(since));
        return this;
    }

    /** Comments under one FoPost post. */
    public InboxListParams postId(String postId) {
        query.put("post_id", postId);
        return this;
    }

    /** Comments under one platform post, including posts not published through FoPost. */
    public InboxListParams postExternalId(String postExternalId) {
        query.put("post_external_id", postExternalId);
        return this;
    }

    /** One DM thread. */
    public InboxListParams conversationId(String conversationId) {
        query.put("conversation_id", conversationId);
        return this;
    }

    /** {@code inbound} or {@code outbound}. */
    public InboxListParams direction(String direction) {
        query.put("direction", direction);
        return this;
    }

    /** {@code newest} or {@code oldest}. */
    public InboxListParams sort(String sort) {
        query.put("sort", sort);
        return this;
    }

    public InboxListParams page(int page) {
        query.put("page", page);
        return this;
    }

    public InboxListParams perPage(int perPage) {
        query.put("per_page", perPage);
        return this;
    }

    public Map<String, Object> toQuery() {
        return new LinkedHashMap<>(query);
    }
}
