package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tells the network to expect heavier spend over a window, so pacing allows for it.
 * {@code budgetValueType} is ABSOLUTE or MULTIPLIER. Times are ISO 8601.
 */
public final class CreateHighDemandPeriodParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateHighDemandPeriodParams() {}

    public static CreateHighDemandPeriodParams of(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String startAt,
            String endAt,
            double budgetValue,
            String budgetValueType) {
        CreateHighDemandPeriodParams params = new CreateHighDemandPeriodParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("startAt", startAt);
        params.body.put("endAt", endAt);
        params.body.put("budgetValue", budgetValue);
        params.body.put("budgetValueType", budgetValueType);
        return params;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
