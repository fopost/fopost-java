package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** A post: its content blocks, the accounts it targets, and its scheduling state. */
public record Post(
        String id,
        String workspaceId,
        String status,
        String contentType,
        Instant scheduleAt,
        Boolean repeatable,
        Integer repeatableTimes,
        Integer repeatableGap,
        String repeatableGapUnit,
        Integer remainingPosts,
        String title,
        String summary,
        Boolean autoPlug,
        String autoPlugContent,
        List<ContentBlock> content,
        List<PostAccount> accounts,
        List<LabelRef> labels,
        Map<String, Object> settings,
        Instant createdAt,
        Instant updatedAt) {}
