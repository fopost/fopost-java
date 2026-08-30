package com.fopost.sdk.model;

import java.time.Instant;

/** A file in the workspace media library. */
public record MediaLibraryItem(
        String id,
        String userId,
        String workspaceId,
        String name,
        String url,
        String type,
        String mimeType,
        Long size,
        String altText,
        Instant createdAt) {}
