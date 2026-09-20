package com.fopost.sdk.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Up to 500 product upserts and deletes in one batch, keyed by your own retailer id. Upserts and
 * deletes travel together. Also needs the {@code publish} scope.
 *
 * <pre>{@code
 * CatalogProductBatchParams.of(workspaceId, connectionId)
 *         .upsert(CatalogProductParams.of(
 *                 "SKU-1042", "Trail Runner",
 *                 "https://yourbrand.com/shop/trail-runner",
 *                 "https://yourbrand.com/img/trail-runner.jpg",
 *                 12900, "USD"))
 *         .delete("SKU-0007");
 * }</pre>
 */
public final class CatalogProductBatchParams {

    private final Map<String, Object> body = new LinkedHashMap<>();
    private final List<Map<String, Object>> products = new ArrayList<>();

    private CatalogProductBatchParams() {}

    public static CatalogProductBatchParams of(String workspaceId, String connectionId) {
        CatalogProductBatchParams params = new CatalogProductBatchParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        return params;
    }

    public CatalogProductBatchParams upsert(CatalogProductParams product) {
        products.add(product.toMap());
        return this;
    }

    public CatalogProductBatchParams delete(String retailerId) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("op", "delete");
        row.put("retailerId", retailerId);
        products.add(row);
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> out = new LinkedHashMap<>(body);
        out.put("products", new ArrayList<>(products));
        return out;
    }
}
