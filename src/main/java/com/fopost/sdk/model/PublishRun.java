package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** One attempt at publishing a post, with a delivery row per targeted account. */
public record PublishRun(
        String id,
        Integer runNumber,
        String status,
        Instant startedAt,
        Instant completedAt,
        List<RunDelivery> deliveries) {

    public record RunDelivery(
            String accountId,
            String accountName,
            String username,
            String platform,
            String status,
            Integer attemptNumber,
            String errorCode,
            String errorMessage,
            String platformPostId,
            String externalUrl,
            Instant startedAt,
            Instant completedAt,
            Long durationMs) {}
}
