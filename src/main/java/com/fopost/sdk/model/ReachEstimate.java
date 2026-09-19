package com.fopost.sdk.model;

/** The audience size range for a targeting. {@code ready} is false while Meta is still sizing it. */
public record ReachEstimate(Long lower, Long upper, Boolean ready) {}
