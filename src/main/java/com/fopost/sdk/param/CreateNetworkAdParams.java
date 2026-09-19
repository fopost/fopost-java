package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An ad inside an ad set, from a creative made with {@code ads().createCreative(...)} or already on
 * the ad account. It starts paused unless {@link #paused(boolean)} is set to false.
 */
public final class CreateNetworkAdParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateNetworkAdParams() {}

    public static CreateNetworkAdParams of(
            String workspaceId, String connectionId, String adSetId, String creativeId, String name) {
        CreateNetworkAdParams params = new CreateNetworkAdParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adSetId", adSetId);
        params.body.put("creativeId", creativeId);
        params.body.put("name", name);
        return params;
    }

    /** False starts delivery at once. Defaults to true. */
    public CreateNetworkAdParams paused(boolean paused) {
        body.put("paused", paused);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
