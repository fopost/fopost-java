package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Counts across every automation the key can reach. */
public record AutomationStats(
        Integer totalAutomations,
        Integer activeAutomations,
        Integer totalRuns24h,
        Map<String, Integer> runsByStatus,
        List<RecentRun> recentRuns) {

    public record RecentRun(Long id, String automationId, String status, Instant startedAt, Instant completedAt) {}
}
