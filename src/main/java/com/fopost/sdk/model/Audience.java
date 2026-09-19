package com.fopost.sdk.model;

/** A saved audience on an ad account. {@code subtype} is CUSTOM, LOOKALIKE or WEBSITE. */
public record Audience(
        String id,
        String name,
        String subtype,
        String description,
        Long sizeLower,
        Long sizeUpper,
        String deliveryStatus,
        String createdAt) {}
