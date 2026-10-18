package com.fopost.sdk.model;

import java.util.List;

/** A stored conversion upload and the hashed events it carried. */
public record ConversionBatch(
        String id,
        String workspaceId,
        String connectionId,
        String adAccountId,
        String pixelId,
        String idempotencyKey,
        long eventCount,
        long accepted,
        List<HashedConversionEvent> events,
        String createdAt) {}
