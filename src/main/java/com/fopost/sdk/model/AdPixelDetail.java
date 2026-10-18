package com.fopost.sdk.model;

/**
 * One pixel in full. {@code code} is the script to place on the site; {@code dataUseSetting} is
 * {@code advertising_and_analytics} or {@code analytics_only}.
 */
public record AdPixelDetail(
        String id,
        String name,
        String code,
        String createdAt,
        String lastFiredAt,
        boolean isUnavailable,
        String dataUseSetting) {}
