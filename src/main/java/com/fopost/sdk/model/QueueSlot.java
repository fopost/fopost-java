package com.fopost.sdk.model;

import java.time.Instant;

/**
 * One recurring weekly posting slot. {@code accountId} is null when the slot applies to every
 * account; {@code weekday} runs 0 (Sunday) to 6 (Saturday) and {@code time} is {@code HH:MM} in
 * {@code timezone}.
 */
public record QueueSlot(
        String id,
        String workspaceId,
        String accountId,
        Integer weekday,
        String time,
        String timezone,
        Instant createdAt) {}
