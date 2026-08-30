package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * A webhook endpoint. {@code secret} is returned only by the create call — store it then,
 * because it is never shown again.
 */
public record Webhook(
        String id,
        String workspaceId,
        String url,
        List<String> events,
        Boolean active,
        String secret,
        Instant lastTriggeredAt,
        Integer failureCount,
        Instant createdAt) {}
