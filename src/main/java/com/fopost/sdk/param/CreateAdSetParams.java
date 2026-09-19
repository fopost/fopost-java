package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An ad set inside a campaign. {@code pageId} is the Page its ads run as; {@code goal} is
 * engagement, traffic, awareness or video_views. It starts paused unless {@link #paused(boolean)}
 * is set to false.
 */
public final class CreateAdSetParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateAdSetParams() {}

    public static CreateAdSetParams of(
            String workspaceId,
            String connectionId,
            String campaignId,
            String pageId,
            String name,
            String goal,
            AdBudgetParams budget,
            AdTargetingParams targeting) {
        CreateAdSetParams params = new CreateAdSetParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("campaignId", campaignId);
        params.body.put("pageId", pageId);
        params.body.put("name", name);
        params.body.put("goal", goal);
        params.body.put("budget", budget.toMap());
        params.body.put("targeting", targeting.toMap());
        return params;
    }

    /** False starts delivery at once. Defaults to true. */
    public CreateAdSetParams paused(boolean paused) {
        body.put("paused", paused);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
