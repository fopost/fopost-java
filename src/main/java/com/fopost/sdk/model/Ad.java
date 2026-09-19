package com.fopost.sdk.model;

import java.time.Instant;

/**
 * A boost or standalone ad created through FoPost.
 *
 * <p>{@code kind} is boost or ad; {@code goal} is engagement, traffic, awareness or video_views.
 * {@code insights} carry the numbers from the last refresh, or null before one.
 */
public record Ad(
        String id,
        String workspaceId,
        String kind,
        String name,
        String goal,
        String status,
        String effectiveStatus,
        String connectionId,
        String accountId,
        String platform,
        String adAccountId,
        String sourcePostId,
        Long budgetMinor,
        String budgetType,
        String currency,
        Instant endAt,
        AdTargeting targeting,
        Creative creative,
        AdInsights insights,
        Instant insightsAt,
        String lastError,
        Instant createdAt) {

    /** The creative of a standalone ad. Null on a boost. */
    public record Creative(
            String text, String headline, String destinationUrl, String mediaUrl, String urlTags) {}
}
