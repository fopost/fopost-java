package com.fopost.sdk.model;

/**
 * One of the account's own videos, resolved from a share link. TikTok serves no raw media file, so
 * {@code downloadUrl} is the share address, which is what a repurpose run reads.
 */
public record TikTokVideoSource(
        String videoId,
        String title,
        String description,
        Integer durationSec,
        String coverImageUrl,
        String shareUrl,
        String embedLink,
        String downloadUrl) {}
