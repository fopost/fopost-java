package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reserves or cancels a reach-and-frequency prediction. Also needs the {@code publish} scope.
 */
public final class ReachFrequencyActionParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private ReachFrequencyActionParams() {}

    public static ReachFrequencyActionParams of(
            String workspaceId,
            String connectionId,
            String adAccountId) {
        ReachFrequencyActionParams params = new ReachFrequencyActionParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        return params;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
