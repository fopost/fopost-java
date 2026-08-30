package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** Follower and reach history for one account, newest first. */
public record AccountAnalyticsHistory(String accountId, String platform, String username, List<Snapshot> history) {

    public record Snapshot(
            Long followers,
            Long following,
            Long totalPosts,
            Long reach,
            Long profileViews,
            Instant fetchedAt) {}
}
