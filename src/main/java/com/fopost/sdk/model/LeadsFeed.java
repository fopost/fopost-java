package com.fopost.sdk.model;

import java.util.List;

/** One page of the leads feed. Pass {@code nextCursor} back as {@code cursor}; null means the end. */
public record LeadsFeed(List<FeedLead> leads, String nextCursor) {}
