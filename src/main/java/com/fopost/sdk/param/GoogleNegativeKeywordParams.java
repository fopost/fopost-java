package com.fopost.sdk.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Add keywords a campaign should never match. */
public final class GoogleNegativeKeywordParams {

    private final Map<String, Object> body;
    private final List<Map<String, Object>> keywords = new ArrayList<>();

    public GoogleNegativeKeywordParams(GoogleAdsScope scope, String sharedSetId) {
        body = scope.toMap();
        body.put("sharedSetId", sharedSetId);
    }

    /** {@code matchType} is EXACT, PHRASE or BROAD. */
    public GoogleNegativeKeywordParams keyword(String text, String matchType) {
        Map<String, Object> keyword = new LinkedHashMap<>();
        keyword.put("text", text);
        keyword.put("matchType", matchType);
        keywords.add(keyword);
        return this;
    }

    public Map<String, Object> toMap() {
        body.put("keywords", keywords);
        return body;
    }
}
