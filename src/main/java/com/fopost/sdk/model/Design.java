package com.fopost.sdk.model;

import java.time.Instant;
import java.util.Map;

/**
 * A design from the create suite: a story, post, carousel, thumbnail, card or collage.
 *
 * @param kind {@code story}, {@code post}, {@code carousel}, {@code thumbnail}, {@code card} or
 *     {@code collage}
 * @param document the design document as the API stores it (pages, layers, size), passed through
 *     unchanged; null on a list, which leaves it out
 * @param thumbnailMediaId the media library item holding the first-page preview, once rendered
 * @param thumbnailUrl a membership-checked URL for that preview
 */
public record Design(
        String id,
        String name,
        String kind,
        Integer width,
        Integer height,
        Map<String, Object> document,
        String thumbnailMediaId,
        String thumbnailUrl,
        Instant createdAt,
        Instant updatedAt) {}
