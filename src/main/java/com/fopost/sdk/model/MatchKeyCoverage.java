package com.fopost.sdk.model;

/** How many of an event's sends carried one identifier, as a percentage. */
public record MatchKeyCoverage(String identifier, double coveragePercent) {}
