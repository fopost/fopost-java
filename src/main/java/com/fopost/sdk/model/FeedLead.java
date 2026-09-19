package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** A lead stored from a subscribed Page. {@code leadId} is Meta's id. */
public record FeedLead(
        String id,
        String leadId,
        String connectionId,
        String pageId,
        String formId,
        String adId,
        String adName,
        String campaignName,
        String platform,
        Boolean isOrganic,
        List<Lead.Field> fields,
        Instant submittedAt,
        String workspaceId) {}
