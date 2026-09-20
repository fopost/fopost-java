package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A product catalog on the connection's business portfolio. Also needs the {@code publish} scope.
 */
public final class CreateCatalogParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateCatalogParams() {}

    public static CreateCatalogParams of(
            String workspaceId,
            String connectionId,
            String name) {
        CreateCatalogParams params = new CreateCatalogParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("name", name);
        return params;
    }

    /** The network's catalog vertical; commerce when unset. */
    public CreateCatalogParams vertical(String vertical) {
        body.put("vertical", vertical);
        return this;
    }
    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
