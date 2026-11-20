package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * A post that uses an export of a design.
 *
 * @param text the start of the post's first block, at most 280 characters
 * @param engagement summed across the post's networks; null until something was measured
 * @param views summed the same way
 */
public record DesignPost(
        String id,
        String status,
        String title,
        String text,
        Instant scheduleAt,
        Instant publishedAt,
        List<String> platforms,
        Long engagement,
        Long views) {}
