package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Options for retrying a post's failed deliveries. */
public final class RetryParams {

    private List<String> accountIds;
    private Boolean includePublished;

    public static RetryParams create() {
        return new RetryParams();
    }

    public RetryParams accountIds(List<String> accountIds) {
        this.accountIds = accountIds;
        return this;
    }

    public RetryParams accountIds(String... accountIds) {
        return accountIds(List.of(accountIds));
    }

    /** Also re-send to accounts that already published. Off by default, and rarely what you want. */
    public RetryParams includePublished(boolean includePublished) {
        this.includePublished = includePublished;
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> body = new LinkedHashMap<>();
        Params.put(body, "accountIds", accountIds);
        Params.put(body, "includePublished", includePublished);
        return body;
    }
}
