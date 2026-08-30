package com.fopost.sdk.model;

import java.time.Instant;

/** One post-to-account delivery, and how far it got. */
public record Delivery(
        String id,
        String accountId,
        String status,
        String platform,
        String username,
        String accountName,
        String errorCode,
        String errorMessage,
        Integer attempts,
        Integer maxAttempts,
        Instant scheduledPublishAt,
        String delayReason,
        String delayMessage,
        Instant postedAt,
        Instant lastAttemptAt,
        String platformPostId,
        String externalUrl) {}
