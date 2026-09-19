package com.fopost.sdk.model;

/** An ad set on a Meta ad account, read live. {@code budgetType} is daily or lifetime. */
public record AdSet(
        String id,
        String name,
        String campaignId,
        String status,
        String effectiveStatus,
        Long budgetMinor,
        String budgetType,
        String endAt,
        String optimizationGoal,
        String createdAt) {}
