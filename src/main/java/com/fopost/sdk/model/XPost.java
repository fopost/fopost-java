package com.fopost.sdk.model;

import java.time.Instant;

/**
 * A post read from X with a connected account's token.
 *
 * <p>{@code conversationId} is the thread root and {@code parentExternalId} the post this one
 * replied to; both are null on a standalone post. The counts are what X reported at read time.
 */
public record XPost(
        String id,
        String text,
        String permalink,
        String authorName,
        String authorHandle,
        String authorAvatarUrl,
        Long authorFollowers,
        String conversationId,
        String parentExternalId,
        Long likes,
        Long replies,
        Long reposts,
        Long quotes,
        Instant platformCreatedAt) {}
