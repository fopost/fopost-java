package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** The targeting to size, on one ad account and Page. */
public final class ReachEstimateParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private ReachEstimateParams() {}

    public static ReachEstimateParams of(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String pageId,
            AdTargetingParams targeting) {
        ReachEstimateParams params = new ReachEstimateParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("pageId", pageId);
        params.body.put("targeting", targeting.toMap());
        return params;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
