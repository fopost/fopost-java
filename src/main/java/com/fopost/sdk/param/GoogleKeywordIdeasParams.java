package com.fopost.sdk.param;

import java.util.List;
import java.util.Map;

/** Ask for keyword ideas from seed terms, a landing page, or both. */
public final class GoogleKeywordIdeasParams {

    private final Map<String, Object> body;

    public GoogleKeywordIdeasParams(GoogleAdsScope scope) {
        body = scope.toMap();
    }

    public GoogleKeywordIdeasParams seeds(List<String> seeds) {
        body.put("seeds", seeds);
        return this;
    }

    public GoogleKeywordIdeasParams url(String url) {
        body.put("url", url);
        return this;
    }

    public GoogleKeywordIdeasParams languageId(String languageId) {
        body.put("languageId", languageId);
        return this;
    }

    public GoogleKeywordIdeasParams geoTargetIds(List<String> geoTargetIds) {
        body.put("geoTargetIds", geoTargetIds);
        return this;
    }

    public Map<String, Object> toMap() {
        return body;
    }
}
