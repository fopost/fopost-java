package com.fopost.sdk.model;

import java.time.Instant;

/**
 * One API request made against a workspace. {@code route} is the pattern, such as
 * {@code /v1/posts/{id}}; {@code message} is the error text of a failed request.
 */
public record LogEntry(
        Long id,
        String workspaceId,
        Instant time,
        String authMethod,
        String method,
        String route,
        String path,
        Integer statusCode,
        Long durationMs,
        String platform,
        String message) {}
