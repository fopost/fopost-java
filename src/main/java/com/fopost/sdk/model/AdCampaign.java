package com.fopost.sdk.model;

/**
 * A campaign on a Meta ad account, read live. {@code status} is ACTIVE, PAUSED, DELETED or
 * ARCHIVED; {@code budgetMinor} is null when the budget lives on the ad sets.
 */
public record AdCampaign(
        String id,
        String name,
        String status,
        String effectiveStatus,
        String objective,
        Long budgetMinor,
        String budgetType,
        String createdAt) {}
