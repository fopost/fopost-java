package com.fopost.sdk.model;

/** One run the network made of a product feed. */
public record ProductFeedUpload(
        String id,
        String startedAt,
        String endedAt,
        String status,
        Long errorCount,
        Long warningCount) {}
