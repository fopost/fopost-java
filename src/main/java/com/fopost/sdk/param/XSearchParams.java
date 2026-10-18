package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An X recent search. {@code accountId} and {@code q} are required: the X account whose token does
 * the read, and a query of 1 to 512 characters in X's own search syntax.
 */
public final class XSearchParams {

    private final Map<String, Object> query = new LinkedHashMap<>();

    public static XSearchParams create(String accountId, String q) {
        XSearchParams params = new XSearchParams();
        params.query.put("account_id", accountId);
        params.query.put("q", q);
        return params;
    }

    /** 10 to 100 posts per page. */
    public XSearchParams maxResults(int maxResults) {
        query.put("max_results", maxResults);
        return this;
    }

    /** The {@code nextToken} from the previous page's meta. */
    public XSearchParams nextToken(String nextToken) {
        Params.put(query, "next_token", nextToken);
        return this;
    }

    public Map<String, Object> toQuery() {
        return new LinkedHashMap<>(query);
    }
}
