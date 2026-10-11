package com.fopost.sdk.model;

import java.time.Instant;

/**
 * A workspace's blog configuration.
 *
 * <p>{@code cadencePerWeek} is how many articles the planner queues each week; 0 turns planning
 * off. {@code reviewHoldDays} is how long a draft waits for review before it may auto-publish.
 * {@code targetKind} is one of {@link BlogTargetKind}. {@code hasWebsiteSecret} says whether a
 * website target has a signing secret; the secret itself is only ever returned when issued.
 * {@code keywordDataAvailable} says whether search volume and difficulty can be fetched on this
 * deployment.
 */
public record BlogSettings(
        String siteUrl,
        String topic,
        String audience,
        String language,
        Integer locationCode,
        int cadencePerWeek,
        int reviewHoldDays,
        boolean autoPublish,
        String imageTier,
        Integer inBodyImages,
        Integer minWords,
        Integer maxWords,
        String brandVoiceId,
        String targetKind,
        String targetAccountId,
        String targetPlatform,
        String websiteUrl,
        boolean hasWebsiteSecret,
        Boolean keywordDataAvailable,
        Instant updatedAt) {}
