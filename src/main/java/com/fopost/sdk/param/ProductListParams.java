package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Filters for listing a store's products. Every field is optional. */
public final class ProductListParams {

    private final Map<String, Object> query = new LinkedHashMap<>();

    public static ProductListParams create() {
        return new ProductListParams();
    }

    /** How many to return, 1 to 50. The API defaults to 20. */
    public ProductListParams limit(int limit) {
        query.put("limit", limit);
        return this;
    }

    /** {@code active}, {@code draft} or {@code archived}. */
    public ProductListParams status(String status) {
        query.put("status", status);
        return this;
    }

    /** Matches the product title. */
    public ProductListParams q(String q) {
        query.put("q", q);
        return this;
    }

    public Map<String, Object> toQuery() {
        return new LinkedHashMap<>(query);
    }
}
