package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Changes to a campaign. Only what is set is sent. */
public final class UpdateAdCampaignParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    public static UpdateAdCampaignParams create() {
        return new UpdateAdCampaignParams();
    }

    public UpdateAdCampaignParams name(String name) {
        body.put("name", name);
        return this;
    }

    /** active or paused. */
    public UpdateAdCampaignParams status(String status) {
        body.put("status", status);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
