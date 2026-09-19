package com.fopost.sdk.model;

import java.util.List;

/**
 * Delivery numbers for one object over a date range. {@code breakdown} is filled when a breakdown
 * was asked for, {@code timeline} when daily numbers were; {@code totals} is null without data.
 */
public record AdInsightsReport(
        String objectId,
        String currency,
        String since,
        String until,
        String breakdownBy,
        Metrics totals,
        List<BreakdownRow> breakdown,
        List<TimelineRow> timeline) {

    /** {@code spendMinor} is in the account currency, minor units; {@code ctr} is a percentage. */
    public record Metrics(Long impressions, Long reach, Long clicks, Long spendMinor, Double ctr, Long leads) {}

    public record BreakdownRow(String key, Metrics metrics) {}

    public record TimelineRow(String date, Metrics metrics) {}
}
