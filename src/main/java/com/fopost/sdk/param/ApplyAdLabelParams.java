package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Puts a label on a campaign, ad set or ad, keeping whatever labels it already carries.
 * {@code level} is campaign, ad_set or ad.
 */
public final class ApplyAdLabelParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private ApplyAdLabelParams() {}

    public static ApplyAdLabelParams of(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String objectId,
            String level) {
        ApplyAdLabelParams params = new ApplyAdLabelParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("objectId", objectId);
        params.body.put("level", level);
        return params;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
