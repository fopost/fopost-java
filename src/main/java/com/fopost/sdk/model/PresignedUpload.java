package com.fopost.sdk.model;

import java.time.Instant;
import java.util.Map;

/**
 * A reserved direct upload. Send the raw bytes to {@code uploadUrl} with {@code method}, exactly
 * {@code headers}, and a {@code Content-Length} equal to the declared size, then complete it.
 */
public record PresignedUpload(
        String uploadId,
        String uploadUrl,
        String method,
        Map<String, String> headers,
        Instant expiresAt) {}
