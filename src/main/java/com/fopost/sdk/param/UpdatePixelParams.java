package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Renames a pixel or changes what its data may be used for. */
public final class UpdatePixelParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private UpdatePixelParams() {}

    public static UpdatePixelParams of(String workspaceId, String connectionId, String adAccountId) {
        UpdatePixelParams params = new UpdatePixelParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        return params;
    }

    public UpdatePixelParams name(String name) {
        body.put("name", name);
        return this;
    }

    /** {@code advertising_and_analytics} or {@code analytics_only}. */
    public UpdatePixelParams dataUseSetting(String dataUseSetting) {
        body.put("dataUseSetting", dataUseSetting);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
