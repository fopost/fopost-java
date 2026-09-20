package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Creates or renames an ad label. */
public final class AdLabelParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private AdLabelParams() {}

    public static AdLabelParams of(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String name) {
        AdLabelParams params = new AdLabelParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("name", name);
        return params;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
