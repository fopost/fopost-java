package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Renames a catalog. Also needs the {@code publish} scope. */
public final class UpdateCatalogParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private UpdateCatalogParams() {}

    public static UpdateCatalogParams of(
            String workspaceId,
            String connectionId,
            String name) {
        UpdateCatalogParams params = new UpdateCatalogParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("name", name);
        return params;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
