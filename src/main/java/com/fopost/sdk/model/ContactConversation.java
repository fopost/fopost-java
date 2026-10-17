package com.fopost.sdk.model;

import java.time.Instant;

/**
 * One thread a contact appears in.
 *
 * <p>{@code key} is how the inbox groups it: the DM thread id, else the post the comments
 * hang off, else the handle. {@code lastItemId} is an inbox item id.
 */
public record ContactConversation(
        String key,
        String accountId,
        String accountUsername,
        String platform,
        int messages,
        int received,
        int sent,
        Instant lastMessageAt,
        String lastItemId) {}
