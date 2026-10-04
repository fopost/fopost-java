package com.fopost.sdk.param;

import java.util.Map;

/**
 * Add a portfolio bid strategy. {@code type} is one of TARGET_SPEND, MAXIMIZE_CONVERSIONS,
 * MAXIMIZE_CONVERSION_VALUE, TARGET_CPA or TARGET_ROAS.
 */
public final class GoogleBidStrategyParams {

    private final Map<String, Object> body;

    public GoogleBidStrategyParams(GoogleAdsScope scope, String name, String type) {
        body = scope.toMap();
        body.put("name", name);
        body.put("type", type);
    }

    /** The account's currency, where the strategy takes a target. */
    public GoogleBidStrategyParams targetMinor(long minor) {
        body.put("targetMinor", minor);
        return this;
    }

    public Map<String, Object> toMap() {
        return body;
    }
}
