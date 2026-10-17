package com.fopost.sdk.model;

/** What this account has published in the rolling window, and what is left. */
public record InstagramPublishingLimit(
        int quotaUsage, Integer quotaTotal, Integer quotaDurationSec, Integer remaining) {}
