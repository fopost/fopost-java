package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A boost of a post FoPost already published. Candidates come from {@code ads().boostable(...)}.
 *
 * <p>{@code goal} is engagement, traffic, awareness or video_views. The boost starts paused
 * unless {@link #paused(boolean)} is set to false.
 */
public final class BoostPostParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private BoostPostParams() {}

    public static BoostPostParams of(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String postId,
            String accountId,
            String name,
            String goal,
            AdBudgetParams budget,
            AdTargetingParams targeting) {
        BoostPostParams params = new BoostPostParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("postId", postId);
        params.body.put("accountId", accountId);
        params.body.put("name", name);
        params.body.put("goal", goal);
        params.body.put("budget", budget.toMap());
        params.body.put("targeting", targeting.toMap());
        return params;
    }

    /** False starts delivery at once. Defaults to true, so nothing is spent until resumed. */
    public BoostPostParams paused(boolean paused) {
        body.put("paused", paused);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
