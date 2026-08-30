package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Reach and engagement for one post, totalled and split per platform. */
public record PostAnalytics(String postId, Totals totals, List<PlatformMetrics> platforms, Instant lastFetchedAt) {

    public record Totals(
            Long impressions,
            Long reach,
            Long engagements,
            Long likes,
            Long comments,
            Long shares,
            Long reposts,
            Long clicks,
            Long saves,
            Long videoViews,
            Long follows) {}

    public record PlatformMetrics(
            String platform,
            String username,
            String externalPostId,
            String permalink,
            String thumbnailUrl,
            String mediaType,
            Instant postedAt,
            Map<String, Object> metrics) {}
}
