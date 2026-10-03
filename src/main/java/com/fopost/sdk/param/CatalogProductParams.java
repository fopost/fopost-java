package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One product in a catalog batch. {@code priceMinor} is minor units of {@code currency}, so 12900
 * with USD is $129.00.
 */
public final class CatalogProductParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CatalogProductParams() {}

    public static CatalogProductParams of(
            String retailerId, String name, String url, String imageUrl, long priceMinor, String currency) {
        CatalogProductParams params = new CatalogProductParams();
        params.body.put("op", "upsert");
        params.body.put("retailerId", retailerId);
        params.body.put("name", name);
        params.body.put("url", url);
        params.body.put("imageUrl", imageUrl);
        params.body.put("priceMinor", priceMinor);
        params.body.put("currency", currency);
        return params;
    }

    public CatalogProductParams description(String description) {
        body.put("description", description);
        return this;
    }

    /** in stock, out of stock, preorder, available for order or discontinued. */
    public CatalogProductParams availability(String availability) {
        body.put("availability", availability);
        return this;
    }

    /** new, refurbished or used. */
    public CatalogProductParams condition(String condition) {
        body.put("condition", condition);
        return this;
    }

    public CatalogProductParams brand(String brand) {
        body.put("brand", brand);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
