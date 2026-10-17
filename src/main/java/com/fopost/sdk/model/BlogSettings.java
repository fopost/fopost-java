package com.fopost.sdk.model;

import java.time.Instant;

/**
 * A workspace's blog configuration.
 *
 * <p>{@code goal} is one of {@link BlogGoal}, or null. {@code cadencePerWeek} is how many articles
 * the planner queues each week; 0 turns planning off. {@code reviewHoldDays} is how many days before
 * its planned date a picked keyword starts being written. {@code targetKind} is one of
 * {@link BlogTargetKind}. {@code hasWebsiteSecret} says whether a
 * website target has a signing secret; the secret itself is only ever returned when issued.
 * {@code planRun} is the last keyword research run, null before the first.
 * {@code keywordDataAvailable} says whether search volume and difficulty can be fetched on this
 * deployment.
 */
public record BlogSettings(
        String siteUrl,
        String topic,
        String audience,
        String goal,
        String focusTopics,
        String avoidTopics,
        String language,
        Integer locationCode,
        int cadencePerWeek,
        int reviewHoldDays,
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
        BlogPlanRun planRun,
        Boolean keywordDataAvailable,
        Instant updatedAt) {}
