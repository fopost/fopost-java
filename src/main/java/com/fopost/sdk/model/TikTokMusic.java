package com.fopost.sdk.model;

/**
 * A track from TikTok's Commercial Music Library. Pass {@code id} as the {@code music_id} platform
 * setting to attach it to a post.
 */
public record TikTokMusic(
        String id,
        String title,
        String author,
        Integer durationSec,
        String coverUrl,
        String previewUrl) {}
