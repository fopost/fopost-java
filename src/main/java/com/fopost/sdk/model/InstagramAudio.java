package com.fopost.sdk.model;

/** A track a Reel can carry; pass {@code id} as the {@code audio_id} platform setting. */
public record InstagramAudio(
        String id,
        String title,
        String artist,
        Integer durationMs,
        String audioType,
        String coverArtworkUrl,
        String previewUrl,
        String username,
        Boolean isAdsEligible) {}
