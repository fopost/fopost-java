package com.fopost.sdk.model;

import java.time.Instant;

/** An X community an account can post into. */
public record Community(
        Long id,
        String accountId,
        String communityId,
        String name,
        Long memberCount,
        String description,
        String imageUrl,
        Instant lastSyncedAt,
        Instant createdAt) {}
