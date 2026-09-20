package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * A series of messages, each a delay after the one before.
 *
 * <p>{@code status} is {@code active} or {@code paused}; a paused sequence fires nothing.
 * {@code workspaceId} is set only on a listing that spans workspaces.
 */
public record Sequence(
        String id,
        String name,
        String accountId,
        List<SequenceStep> steps,
        String status,
        Instant createdAt,
        EnrollmentCounts enrollments,
        String workspaceId) {

    public Sequence {
        steps = steps == null ? List.of() : List.copyOf(steps);
    }
}
