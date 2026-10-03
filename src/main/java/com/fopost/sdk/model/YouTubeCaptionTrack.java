package com.fopost.sdk.model;

/** A caption track on one of the channel's videos; {@code language} is a BCP-47 tag. */
public record YouTubeCaptionTrack(
        String id,
        String language,
        String name,
        String trackKind,
        boolean isDraft,
        boolean isAutoSynced,
        String lastUpdated) {}
