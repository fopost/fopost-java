package com.fopost.sdk.model;

import java.time.Instant;

/** Token validity for one account. {@code healthStatus} is healthy, degraded, expired, revoked or unknown. */
public record AccountHealth(
        String id,
        String workspaceId,
        String platform,
        String username,
        Boolean active,
        String healthStatus,
        Instant lastHealthCheck) {}
