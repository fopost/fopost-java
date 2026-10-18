package com.fopost.sdk.model;

import java.util.List;

/** How often each event fired between {@code since} and {@code until} (the last 28 days). */
public record AdPixelStats(String since, String until, List<PixelEventCount> events) {}
