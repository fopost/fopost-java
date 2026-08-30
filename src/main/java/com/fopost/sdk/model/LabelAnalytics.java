package com.fopost.sdk.model;

/** Campaign roll-up for one label. */
public record LabelAnalytics(
        String labelId,
        String name,
        String color,
        Integer postCount,
        Long impressions,
        Long reach,
        Long engagements,
        Long likes,
        Long comments,
        Long shares,
        Double engagementRate,
        Long followerDelta) {}
