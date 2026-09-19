package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** The date range of an insights report, with an optional breakdown or daily timeline. */
public final class AdInsightsParams {

    private final Map<String, Object> query = new LinkedHashMap<>();

    private AdInsightsParams() {}

    /** Dates as {@code YYYY-MM-DD}. */
    public static AdInsightsParams of(String since, String until) {
        AdInsightsParams params = new AdInsightsParams();
        params.query.put("since", since);
        params.query.put("until", until);
        return params;
    }

    /** age, gender, placement or country. */
    public AdInsightsParams breakdown(String breakdown) {
        query.put("breakdown", breakdown);
        return this;
    }

    /** True adds a per-day timeline. */
    public AdInsightsParams daily(boolean daily) {
        query.put("daily", daily);
        return this;
    }

    public Map<String, Object> toQuery() {
        return new LinkedHashMap<>(query);
    }
}
