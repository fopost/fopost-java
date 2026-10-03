package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A product set: the slice of a catalog one catalog ad runs from. Without a
 * {@link #filter(java.util.Map)} the set is the whole catalog. Also needs the {@code publish} scope.
 */
public final class ProductSetParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private ProductSetParams() {}

    public static ProductSetParams of(
            String workspaceId,
            String connectionId,
            String name) {
        ProductSetParams params = new ProductSetParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("name", name);
        return params;
    }

    /** The network's own product-set filter. */
    public ProductSetParams filter(java.util.Map<String, Object> filter) {
        body.put("filter", filter);
        return this;
    }
    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
