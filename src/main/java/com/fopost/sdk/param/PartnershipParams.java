package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Asks a creator for partnership permission. */
public final class PartnershipParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private PartnershipParams() {}

    public static PartnershipParams of(
            String workspaceId,
            String connectionId,
            String pageId,
            String creatorId) {
        PartnershipParams params = new PartnershipParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("pageId", pageId);
        params.body.put("creatorId", creatorId);
        return params;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
