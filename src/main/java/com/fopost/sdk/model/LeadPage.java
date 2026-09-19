package com.fopost.sdk.model;

import java.time.Instant;

/** A Page whose new leads FoPost stores as they arrive. */
public record LeadPage(
        String connectionId, String pageId, String pageName, Instant createdAt, String workspaceId) {}
