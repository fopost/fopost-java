package com.fopost.sdk.param;

import java.util.LinkedHashMap;
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

    /** {@code comment}, {@code mention} or {@code dm}. */
    public InboxListParams type(String type) {
        query.put("type", type);
        return this;
    }

    /** {@code unread}, {@code read}, {@code resolved} or {@code snoozed}. */
    public InboxListParams state(String state) {
        query.put("state", state);
        return this;
    }

    public InboxListParams platform(String platform) {
        query.put("platform", platform);
        return this;
    }

    public InboxListParams accountId(String accountId) {
        query.put("account_id", accountId);
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

    public InboxListParams q(String q) {
        query.put("q", q);
        return this;
    }

    /** {@code newest}, {@code oldest} or {@code unanswered}. */
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
