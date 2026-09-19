package com.fopost.sdk.model;

import java.time.Instant;

/** A post on the account that never went out through FoPost, with the freshest reading held. */
public record NativePost(
        String externalPostId,
        String text,
        String permalink,
        String thumbnailUrl,
        String mediaType,
        Instant postedAt,
        Instant fetchedAt,
        Metrics metrics) {

    public record Metrics(
            Integer impressions,
            Integer reach,
            Integer engagements,
            Integer likes,
            Integer comments,
            Integer shares,
            Integer videoViews) {}
}
