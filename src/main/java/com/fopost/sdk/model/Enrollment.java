package com.fopost.sdk.model;

import java.time.Instant;

/**
 * One contact walking one sequence.
 *
 * <p>{@code step} counts the steps already sent, so it is also the index of the next one.
 * {@code status} is {@code active}, {@code completed}, {@code stopped} or {@code failed}.
 * {@code error} carries the reason when a step was skipped rather than sent.
 */
public record Enrollment(
        String id,
        String contactId,
        String displayName,
        int step,
        Instant nextAt,
        String status,
        Instant lastSentAt,
        String error) {}
