package com.fopost.sdk.model;

/** One event of a stored batch. Identifiers are kept only as SHA-256 hashes. */
public record HashedConversionEvent(
        String eventName,
        String occurredAt,
        String eventId,
        String actionSource,
        String emailHash,
        String phoneHash,
        String externalIdHash,
        Long valueMinor,
        String currency,
        String orderId) {}
