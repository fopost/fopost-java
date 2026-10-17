package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Prices a flight before you buy it. Nothing is reserved until you reserve it. Times are ISO 8601.
 */
public final class CreateReachFrequencyParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateReachFrequencyParams() {}

    public static CreateReachFrequencyParams of(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String name,
            AdTargetingParams targeting,
            List<String> placements,
            long budgetMinor,
            String startAt,
            String endAt) {
        CreateReachFrequencyParams params = new CreateReachFrequencyParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("name", name);
        params.body.put("targeting", targeting.toMap());
        params.body.put("placements", placements);
        params.body.put("budgetMinor", budgetMinor);
        params.body.put("startAt", startAt);
        params.body.put("endAt", endAt);
        return params;
    }

    /** How often one person should see the ad over the flight. */
    public CreateReachFrequencyParams frequencyCap(int frequencyCap) {
        body.put("frequencyCap", frequencyCap);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
