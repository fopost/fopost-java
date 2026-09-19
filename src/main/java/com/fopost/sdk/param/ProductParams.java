package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The body of an update on a store product. Only what is set travels, so an
 * omitted field keeps whatever the store already had. Set at least one.
 */
public final class ProductParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    public static ProductParams create() {
        return new ProductParams();
    }

    public ProductParams title(String title) {
        body.put("title", title);
        return this;
    }

    /** Body markup, rendered to HTML on the store. */
    public ProductParams description(String description) {
        body.put("description", description);
        return this;
    }

    /** {@code active}, {@code draft} or {@code archived}. */
    public ProductParams status(String status) {
        body.put("status", status);
        return this;
    }

    public ProductParams tags(List<String> tags) {
        body.put("tags", tags);
        return this;
    }

    public ProductParams productType(String productType) {
        body.put("product_type", productType);
        return this;
    }

    public ProductParams vendor(String vendor) {
        body.put("vendor", vendor);
        return this;
    }

    public Map<String, Object> toBody() {
        return new LinkedHashMap<>(body);
    }
}
