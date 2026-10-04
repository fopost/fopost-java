package com.fopost.sdk.model;

/** A playlist on the channel; {@code isDefault} marks the one a new video joins when none is picked. */
public record YouTubePlaylist(
        String id,
        String title,
        String description,
        String privacy,
        Integer itemCount,
        String thumbnailUrl,
        boolean isDefault) {}
