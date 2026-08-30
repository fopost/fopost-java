package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** Latest per-account figures for a workspace, plus the workspace totals. */
public record WorkspaceAnalytics(String workspaceId, List<AccountSnapshot> accounts, Totals totals) {

    public record AccountSnapshot(
            String accountId,
            String platform,
            String username,
            Long followers,
            Long following,
            Long totalPosts,
            Instant fetchedAt) {}

    public record Totals(Long followers, Long totalPosts) {}
}
