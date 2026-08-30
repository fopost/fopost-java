package com.fopost.sdk.model;

import java.time.Instant;

/** An account a post targets, plus that account's delivery state. */
public record PostAccount(
        String id,
        String platform,
        String username,
        String name,
        String avatar,
        String publishStatus,
        Instant postedAt,
        String platformPostId,
        String externalUrl,
        String errorCode,
        String errorMessage,
        String rawErrorMessage,
        Integer attempts,
        Integer maxAttempts) {}
