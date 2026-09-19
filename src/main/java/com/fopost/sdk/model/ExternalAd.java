package com.fopost.sdk.model;

import java.time.Instant;

/** An ad on a connected ad account that was not created through FoPost. Read live, never stored. */
public record ExternalAd(
        String id,
        String name,
        String effectiveStatus,
        String campaignId,
        String campaignName,
        String objective,
        Long budgetMinor,
        String budgetType,
        Instant endAt,
        Instant createdAt,
        String connectionId,
        String adAccountId,
        String currency,
        String workspaceId) {}
