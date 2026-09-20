package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A product feed. A {@link #schedule(String)} needs a {@link #url(String)}. Also needs the
 * {@code publish} scope.
 */
public final class CreateProductFeedParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateProductFeedParams() {}

    public static CreateProductFeedParams of(
            String workspaceId,
            String connectionId,
            String name) {
        CreateProductFeedParams params = new CreateProductFeedParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("name", name);
        return params;
    }

    /** Where the network fetches the file; omit for manual uploads. */
    public CreateProductFeedParams url(String url) {
        body.put("url", url);
        return this;
    }

    /** HOURLY, DAILY or WEEKLY. Needs a url. */
    public CreateProductFeedParams schedule(String schedule) {
        body.put("schedule", schedule);
        return this;
    }
    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
