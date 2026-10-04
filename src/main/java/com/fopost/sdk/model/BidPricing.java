package com.fopost.sdk.model;

/** What the auction costs, in minor units of the ad account currency. */
public record BidPricing(
        String currency,
        Long suggestedBidMinor,
        Long minBidMinor,
        Long maxBidMinor,
        Long dailyBudgetFloorMinor) {}
