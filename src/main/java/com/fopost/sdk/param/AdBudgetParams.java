package com.fopost.sdk.param;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/** The budget of a boost or ad. {@code minor} is in the ad account currency, minor units. */
public final class AdBudgetParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private AdBudgetParams(long minor, String type) {
        body.put("minor", minor);
        body.put("type", type);
    }

    public static AdBudgetParams daily(long minor) {
        return new AdBudgetParams(minor, "daily");
    }

    public static AdBudgetParams lifetime(long minor) {
        return new AdBudgetParams(minor, "lifetime");
    }

    /** When delivery stops. */
    public AdBudgetParams endAt(Instant endAt) {
        body.put("endAt", Params.iso(endAt));
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
