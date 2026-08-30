package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Posts with their per-platform delivery breakdown, as the analytics table renders them. */
public record PostsTable(List<Row> posts, Integer total, Integer page, Integer limit, StatusSummary statusSummary) {

    public record Row(
            String postId,
            String preview,
            String status,
            Instant createdAt,
            Instant scheduledAt,
            List<Map<String, Object>> platforms,
            Map<String, Object> deliverySummary) {}

    public record StatusSummary(
            Integer draft,
            Integer scheduled,
            Integer published,
            Integer failed,
            Integer pending,
            Integer total) {}
}
