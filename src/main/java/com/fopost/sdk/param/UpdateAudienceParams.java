package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Changes to a saved audience. Only what is set is sent. */
public final class UpdateAudienceParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    public static UpdateAudienceParams create() {
        return new UpdateAudienceParams();
    }

    public UpdateAudienceParams name(String name) {
        body.put("name", name);
        return this;
    }

    public UpdateAudienceParams description(String description) {
        body.put("description", description);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
