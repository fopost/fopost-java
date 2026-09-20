package com.fopost.sdk.model;

import java.util.List;

/** Inbox analytics broken out per thread. */
public record ConversationAnalytics(
        List<ConversationAnalyticsRow> conversations, int total, int page, int perPage) {

    public ConversationAnalytics {
        conversations = conversations == null ? List.of() : List.copyOf(conversations);
    }
}
