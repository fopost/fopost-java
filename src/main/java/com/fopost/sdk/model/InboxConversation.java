package com.fopost.sdk.model;

import java.time.Instant;

/** One direct-message thread with a participant. */
public record InboxConversation(
        String workspaceId,
        String accountId,
        String conversationId,
        Integer messageCount,
        Integer unreadCount,
        Instant lastMessageAt,
        String lastMessageText,
        Boolean lastMessageOutbound,
        Participant participant,
        InboxAccountRef account) {

    public record Participant(String name, String handle, String avatarUrl) {}
}
