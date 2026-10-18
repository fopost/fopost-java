package com.fopost.sdk.model;

/** A conversion the account defines on top of a pixel. {@code defaultValueMinor} is in minor units. */
public record CustomConversion(
        String id,
        String name,
        String pixelId,
        String eventType,
        CustomConversionRule rule,
        String description,
        Long defaultValueMinor,
        boolean isArchived,
        boolean isUnavailable,
        String createdAt,
        String lastFiredAt) {}
