package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Changes a custom conversion's name, description or default value. The rule cannot change. */
public final class UpdateCustomConversionParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private UpdateCustomConversionParams() {}

    public static UpdateCustomConversionParams of(String workspaceId, String connectionId, String adAccountId) {
        UpdateCustomConversionParams params = new UpdateCustomConversionParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        return params;
    }

    public UpdateCustomConversionParams name(String name) {
        body.put("name", name);
        return this;
    }

    public UpdateCustomConversionParams description(String description) {
        body.put("description", description);
        return this;
    }

    public UpdateCustomConversionParams defaultValueMinor(long defaultValueMinor) {
        body.put("defaultValueMinor", defaultValueMinor);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
