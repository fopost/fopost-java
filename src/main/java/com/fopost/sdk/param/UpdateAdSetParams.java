package com.fopost.sdk.param;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/** Changes to an ad set. Only what is set is sent. */
public final class UpdateAdSetParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    public static UpdateAdSetParams create() {
        return new UpdateAdSetParams();
    }

    public UpdateAdSetParams name(String name) {
        body.put("name", name);
        return this;
    }

    /** active or paused. */
    public UpdateAdSetParams status(String status) {
        body.put("status", status);
        return this;
    }

    /** The new budget in minor units; the budget type set at creation stays. */
    public UpdateAdSetParams budgetMinor(long budgetMinor) {
        body.put("budgetMinor", budgetMinor);
        return this;
    }

    public UpdateAdSetParams endAt(Instant endAt) {
        Params.put(body, "endAt", Params.iso(endAt));
        return this;
    }

    public UpdateAdSetParams targeting(AdTargetingParams targeting) {
        body.put("targeting", targeting.toMap());
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
