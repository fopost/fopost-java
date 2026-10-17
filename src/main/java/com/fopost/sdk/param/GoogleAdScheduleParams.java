package com.fopost.sdk.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Replace a campaign's ad schedule. Google has no partial edit for one, so every slot the
 * campaign should keep has to be here.
 */
public final class GoogleAdScheduleParams {

    private final Map<String, Object> body;
    private final List<Map<String, Object>> slots = new ArrayList<>();

    public GoogleAdScheduleParams(GoogleAdsScope scope, String campaignId) {
        body = scope.toMap();
        body.put("campaignId", campaignId);
    }

    /** {@code dayOfWeek} is MONDAY through SUNDAY; the hours are 0 to 24. */
    public GoogleAdScheduleParams slot(String dayOfWeek, int startHour, int endHour) {
        return slot(dayOfWeek, startHour, endHour, null);
    }

    public GoogleAdScheduleParams slot(
            String dayOfWeek, int startHour, int endHour, Double bidModifier) {
        Map<String, Object> slot = new LinkedHashMap<>();
        slot.put("dayOfWeek", dayOfWeek);
        slot.put("startHour", startHour);
        slot.put("endHour", endHour);
        if (bidModifier != null) {
            slot.put("bidModifier", bidModifier);
        }
        slots.add(slot);
        return this;
    }

    public Map<String, Object> toMap() {
        body.put("slots", slots);
        return body;
    }
}
