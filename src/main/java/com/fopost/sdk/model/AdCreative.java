package com.fopost.sdk.model;

/** A creative on a Meta ad account. {@code format} is image, video, carousel, post or other. */
public record AdCreative(
        String id,
        String name,
        String format,
        String status,
        String title,
        String body,
        String link,
        String thumbnailUrl,
        String callToAction,
        String urlTags) {}
