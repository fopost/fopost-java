package com.fopost.sdk.model;

import java.time.Instant;

/**
 * How one thread performed over the period.
 *
 * <p>{@code medianResponseMinutes} is null when the thread was never answered.
 */
public record ConversationAnalyticsRow(
        String key,
        String accountId,
        String platform,
        int received,
        int sent,
        int answered,
        int open,
        Double medianResponseMinutes,
        Instant firstMessageAt,
        Instant lastMessageAt) {}
