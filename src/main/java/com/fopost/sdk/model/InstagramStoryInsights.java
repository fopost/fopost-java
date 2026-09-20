package com.fopost.sdk.model;

import java.util.Map;

/** The insight set for one story. */
public record InstagramStoryInsights(String storyId, Map<String, Integer> insights) {}
