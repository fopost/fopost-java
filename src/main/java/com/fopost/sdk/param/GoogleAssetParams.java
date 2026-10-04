package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Add an asset to the library. The kind picks which other fields apply. */
public final class GoogleAssetParams {

    private final Map<String, Object> body;
    private final Map<String, Object> spec = new LinkedHashMap<>();

    private GoogleAssetParams(GoogleAdsScope scope, String kind) {
        body = scope.toMap();
        spec.put("kind", kind);
    }

    /** A link under the ad, with its own destination. */
    public static GoogleAssetParams sitelink(GoogleAdsScope scope, String text, String finalUrl) {
        GoogleAssetParams params = new GoogleAssetParams(scope, "sitelink");
        params.spec.put("text", text);
        params.spec.put("finalUrl", finalUrl);
        return params;
    }

    /** A short phrase beside the ad. */
    public static GoogleAssetParams callout(GoogleAdsScope scope, String text) {
        GoogleAssetParams params = new GoogleAssetParams(scope, "callout");
        params.spec.put("text", text);
        return params;
    }

    /** A header and the values listed under it. */
    public static GoogleAssetParams snippet(
            GoogleAdsScope scope, String header, List<String> values) {
        GoogleAssetParams params = new GoogleAssetParams(scope, "snippet");
        params.spec.put("header", header);
        params.spec.put("values", values);
        return params;
    }

    /** The lines under a sitelink. */
    public GoogleAssetParams descriptions(String description1, String description2) {
        spec.put("description1", description1);
        spec.put("description2", description2);
        return this;
    }

    public Map<String, Object> toMap() {
        body.put("spec", spec);
        return body;
    }
}
