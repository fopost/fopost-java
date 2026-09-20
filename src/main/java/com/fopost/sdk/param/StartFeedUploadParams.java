package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Fetches a feed now. Also needs the {@code publish} scope. */
public final class StartFeedUploadParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private StartFeedUploadParams() {}

    public static StartFeedUploadParams of(
            String workspaceId,
            String connectionId) {
        StartFeedUploadParams params = new StartFeedUploadParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        return params;
    }

    /** Overrides the feed's own url for this run. */
    public StartFeedUploadParams url(String url) {
        body.put("url", url);
        return this;
    }
    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
