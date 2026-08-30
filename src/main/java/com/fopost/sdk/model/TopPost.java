package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** A high-performing post, ranked by the metric the request sorted on. */
public record TopPost(
        Integer rank,
        String postId,
        String externalPostId,
        String source,
        String preview,
        String permalink,
        String thumbnailUrl,
        String status,
        Instant createdAt,
        List<PlatformRef> platforms,
        List<LabelRef> labels,
        Metrics metrics) {

    public record PlatformRef(String platform, String username, String url) {}

    public record Metrics(
            Long engagements,
            Long impressions,
            Long reach,
            Long likes,
            Long comments,
            Long shares,
            Long reposts,
            Long clicks,
            Long saves,
            Long videoViews) {}
}
