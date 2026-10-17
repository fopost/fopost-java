package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A standalone ad built from a creative.
 *
 * <p>{@code goal} is engagement, traffic, awareness or video_views; {@code text} is the primary
 * copy, up to 125 characters. The ad starts paused unless {@link #paused(boolean)} is set to
 * false.
 */
public final class CreateAdParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateAdParams() {}

    public static CreateAdParams of(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String pageId,
            String name,
            String goal,
            AdBudgetParams budget,
            AdTargetingParams targeting,
            String text) {
        CreateAdParams params = new CreateAdParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("pageId", pageId);
        params.body.put("name", name);
        params.body.put("goal", goal);
        params.body.put("budget", budget.toMap());
        params.body.put("targeting", targeting.toMap());
        params.body.put("text", text);
        return params;
    }

    /**
     * Run a post already live on the network as a Spark ad, from
     * {@code ads().sparkPosts(...)}. The post carries its own caption and
     * media, so {@code text}, {@code headline} and {@code mediaUrl} are
     * ignored. Needs the network's {@code sparkAds} capability.
     */
    public CreateAdParams sparkPostId(String sparkPostId) {
        body.put("sparkPostId", sparkPostId);
        return this;
    }

    public CreateAdParams headline(String headline) {
        body.put("headline", headline);
        return this;
    }

    public CreateAdParams destinationUrl(String destinationUrl) {
        body.put("destinationUrl", destinationUrl);
        return this;
    }

    /** A media library asset url. */
    public CreateAdParams mediaUrl(String mediaUrl) {
        body.put("mediaUrl", mediaUrl);
        return this;
    }

    /** A query string appended to every link in the ad, e.g. {@code utm_source=meta&utm_medium=paid}. */
    public CreateAdParams urlTags(String urlTags) {
        body.put("urlTags", urlTags);
        return this;
    }

    /** False starts delivery at once. Defaults to true, so nothing is spent until resumed. */
    public CreateAdParams paused(boolean paused) {
        body.put("paused", paused);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
