package com.fopost.sdk.model;

import java.util.Map;

/** A story still inside its 24 hours; {@code insights} is present only when asked for. */
public record InstagramStory(
        String id,
        String mediaType,
        String mediaProductType,
        String permalink,
        String mediaUrl,
        String thumbnailUrl,
        String caption,
        String timestamp,
        Map<String, Integer> insights) {}
