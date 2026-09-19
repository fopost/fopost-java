package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Changes to an ad inside an ad set. Only what is set is sent. */
public final class UpdateNetworkAdParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    public static UpdateNetworkAdParams create() {
        return new UpdateNetworkAdParams();
    }

    public UpdateNetworkAdParams name(String name) {
        body.put("name", name);
        return this;
    }

    /** active or paused. */
    public UpdateNetworkAdParams status(String status) {
        body.put("status", status);
        return this;
    }

    /** Swap the creative the ad shows. */
    public UpdateNetworkAdParams creativeId(String creativeId) {
        body.put("creativeId", creativeId);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
