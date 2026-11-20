package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * A video edit job: the source video, the steps, and what they produced.
 *
 * @param status {@code queued}, {@code running}, {@code succeeded} or {@code failed}
 * @param progress 0 to 100
 * @param resultMediaId the media library item holding the edited video, once it succeeded
 * @param extraOutputs further files a step produced, such as clips or a subtitle file
 * @param result step-specific output, such as a transcript; null until there is one
 * @param error why it failed, in plain words
 */
public record VideoEdit(
        String id,
        String status,
        Integer progress,
        String sourceMediaId,
        List<VideoEditOperation> operations,
        String resultMediaId,
        ResultMedia resultMedia,
        List<ExtraOutput> extraOutputs,
        Map<String, Object> result,
        String error,
        Instant createdAt,
        Instant updatedAt,
        Instant completedAt) {

    public record ResultMedia(String id, String previewUrl, String mimeType) {}

    public record ExtraOutput(String mediaId, String label, String mime, String previewUrl) {}
}
