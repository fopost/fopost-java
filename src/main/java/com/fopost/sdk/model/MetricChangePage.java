package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * Readings recorded after a cursor, oldest first.
 *
 * <p>Feed {@code cursor} back as the next {@code since} to continue; it is null when nothing
 * changed.
 */
public record MetricChangePage(Instant since, Instant cursor, Boolean hasMore, List<Change> changes) {

    /** One reading. {@code postId} is null for a post made natively on the network. */
    public record Change(
            String accountId,
            String platform,
            String externalPostId,
            String postId,
            Instant postedAt,
            Instant fetchedAt,
            Integer impressions,
            Integer reach,
            Integer engagements,
            Integer likes,
            Integer comments,
            Integer shares) {}
}
