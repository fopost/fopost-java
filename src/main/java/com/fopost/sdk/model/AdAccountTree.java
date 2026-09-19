package com.fopost.sdk.model;

import java.util.List;

/** Every campaign on an ad account, with its ad sets and their ads. */
public record AdAccountTree(String adAccountId, String currency, String workspaceId, List<Campaign> campaigns) {

    public record Campaign(
            String id,
            String name,
            String status,
            String effectiveStatus,
            String objective,
            Long budgetMinor,
            String budgetType,
            String createdAt,
            List<AdSetNode> adSets) {}

    public record AdSetNode(
            String id,
            String name,
            String campaignId,
            String status,
            String effectiveStatus,
            Long budgetMinor,
            String budgetType,
            String endAt,
            String optimizationGoal,
            String createdAt,
            List<NetworkAd> ads) {}
}
