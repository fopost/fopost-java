package com.fopost.sdk.model;

/**
 * What an audience would deliver at a budget, over the network's own window. {@code ready} is
 * false while the network has no answer for that audience.
 */
public record SupplyForecast(
        String currency,
        Long impressions,
        Long clicks,
        Long spendMinor,
        Long windowDays,
        Boolean ready) {}
