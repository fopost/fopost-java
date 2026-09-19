package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Filters for listing a blog's articles. Every field is optional. */
public final class ArticleListParams {

    private final Map<String, Object> query = new LinkedHashMap<>();

    public static ArticleListParams create() {
        return new ArticleListParams();
    }

    /** How many to return, 1 to 50. The API defaults to 20. */
    public ArticleListParams limit(int limit) {
        query.put("limit", limit);
        return this;
    }

    /** {@code published}, {@code draft}, {@code pending} or {@code scheduled}. */
    public ArticleListParams status(String status) {
        query.put("status", status);
        return this;
    }

    /** Matches the article title. */
    public ArticleListParams q(String q) {
        query.put("q", q);
        return this;
    }

    public Map<String, Object> toQuery() {
        return new LinkedHashMap<>(query);
    }
}
