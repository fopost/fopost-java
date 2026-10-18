package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** The slot after an update, plus the queued posts the change moved. */
public record QueueSlotUpdate(
        String id,
        String workspaceId,
        String accountId,
        Integer weekday,
        String time,
        String timezone,
        Instant createdAt,
        List<QueueSlotMove> moved) {}
