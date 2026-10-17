package com.fopost.sdk.model;

import java.time.Instant;

/** One thing that happened in a workspace. A {@code security} kind is an audit row. */
public record ActivityEvent(
        String id,
        String workspaceId,
        String kind,
        String refType,
        String refId,
        String summary,
        ActivityActor actor,
        Instant time) {}
