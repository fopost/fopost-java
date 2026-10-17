package com.fopost.sdk.model;

/** A priced flight. Nothing is bought until it is reserved. */
public record ReachFrequencyPrediction(
        String id,
        String name,
        String status,
        Long reach,
        Long impressions,
        Long frequencyCap,
        Long budgetMinor,
        String startAt,
        String endAt,
        boolean reserved) {}
