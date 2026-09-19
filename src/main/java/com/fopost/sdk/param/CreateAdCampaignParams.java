package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A campaign on a Meta ad account. {@code goal} is engagement, traffic, awareness or video_views.
 * It starts paused unless {@link #paused(boolean)} is set to false.
 */
public final class CreateAdCampaignParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateAdCampaignParams() {}

    public static CreateAdCampaignParams of(
            String workspaceId, String connectionId, String adAccountId, String name, String goal) {
        CreateAdCampaignParams params = new CreateAdCampaignParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("name", name);
        params.body.put("goal", goal);
        return params;
    }

    /** False starts delivery at once. Defaults to true. */
    public CreateAdCampaignParams paused(boolean paused) {
        body.put("paused", paused);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
