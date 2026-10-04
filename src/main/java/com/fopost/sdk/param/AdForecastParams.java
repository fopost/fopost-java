package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The shared body of a bid-pricing or supply-forecast request. {@code bidType} applies to bid
 * pricing only, {@code budgetMinor} to the supply forecast only.
 */
public final class AdForecastParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private AdForecastParams() {}

    /** {@code goal} is engagement, traffic, awareness or video_views. */
    public static AdForecastParams of(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String goal,
            AdTargetingParams targeting) {
        AdForecastParams params = new AdForecastParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("goal", goal);
        params.body.put("targeting", targeting.toMap());
        return params;
    }

    public AdForecastParams placements(List<String> placements) {
        body.put("placements", List.copyOf(placements));
        return this;
    }

    /** CPC, CPM or CPV. Bid pricing only. */
    public AdForecastParams bidType(String bidType) {
        body.put("bidType", bidType);
        return this;
    }

    /** The budget for the forecast window, minor units. Supply forecast only. */
    public AdForecastParams budgetMinor(long budgetMinor) {
        body.put("budgetMinor", budgetMinor);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
