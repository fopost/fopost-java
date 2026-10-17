package com.fopost.sdk.model;

import java.time.Instant;

/**
 * One message, sent into conversations the workspace already has.
 *
 * <p>{@code name} is internal only and is never sent to anyone. {@code status} is
 * {@code draft}, {@code scheduled}, {@code sending}, {@code sent} or {@code cancelled}.
 * {@code workspaceId} is set only on a listing that spans workspaces.
 */
public record Broadcast(
        String id,
        String name,
        String text,
        String accountId,
        AudienceFilter audience,
        String status,
        Instant scheduledAt,
        Instant sentAt,
        Instant createdAt,
        BroadcastCounts counts,
        String workspaceId) {}
