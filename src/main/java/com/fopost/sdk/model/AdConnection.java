package com.fopost.sdk.model;

import java.time.Instant;

/** A connected ads account grant. */
public record AdConnection(
        String id,
        String provider,
        String authType,
        String name,
        String businessId,
        Instant createdAt,
        String workspaceId) {}
