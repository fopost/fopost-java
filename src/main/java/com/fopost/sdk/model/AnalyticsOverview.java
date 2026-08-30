package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** Headline totals for the requested window, with the per-platform and per-account breakdown. */
public record AnalyticsOverview(
        Integer totalAccounts,
        Long totalFollowers,
        Long totalPosts,
        Long totalEngagement,
        Long totalImpressions,
        Long totalReach,
        Long totalLikes,
        Long totalComments,
        Long totalShares,
        Long totalReposts,
        Long totalSaves,
        Long totalClicks,
        Long totalVideoViews,
        Long totalProfileViews,
        Double engagementRate,
        Deltas deltas,
        TodayStats todayStats,
        List<PlatformTotals> platforms,
        List<AccountTotals> accounts) {

    public record Deltas(
            Double followers,
            Double posts,
            Double engagement,
            Double impressions,
            Double likes,
            Double comments,
            Double shares,
            Double profileViews) {}

    public record TodayStats(Integer posts, Integer followerChange, Integer engagement) {}

    public record PlatformTotals(String platform, Integer accounts, Long followers) {}

    public record AccountTotals(
            String accountId,
            String platform,
            String username,
            String name,
            String avatar,
            Long followers,
            Long totalPosts,
            Instant fetchedAt) {}
}
