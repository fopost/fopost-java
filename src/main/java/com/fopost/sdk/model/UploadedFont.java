package com.fopost.sdk.model;

import java.time.Instant;

/**
 * A font uploaded into a workspace.
 *
 * @param style {@code normal} or {@code italic}
 * @param format the stored file format, such as {@code woff2} or {@code ttf}
 * @param fileUrl where the file is served, after a membership check
 */
public record UploadedFont(
        String id, String family, String style, Integer weight, String format, String fileUrl, Instant createdAt) {}
