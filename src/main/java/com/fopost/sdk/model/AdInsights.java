package com.fopost.sdk.model;

/** Lifetime delivery numbers. {@code spendMinor} is in the ad account currency, minor units. */
public record AdInsights(Long impressions, Long reach, Long clicks, Long spendMinor) {}
