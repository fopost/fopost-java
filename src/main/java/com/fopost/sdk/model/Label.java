package com.fopost.sdk.model;

import java.time.Instant;

/** A workspace label. {@code workspace} is either the workspace id or a short reference object. */
public record Label(
        String id,
        String name,
        String color,
        Object workspace,
        Instant createdAt,
        Instant updatedAt) {}
