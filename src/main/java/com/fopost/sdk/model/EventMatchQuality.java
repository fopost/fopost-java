package com.fopost.sdk.model;

import java.util.List;

/** The network's match score for one event, out of 10, and which identifiers were sent. */
public record EventMatchQuality(String eventName, Double score, List<MatchKeyCoverage> matchKeys) {}
