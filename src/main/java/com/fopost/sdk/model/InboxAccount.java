package com.fopost.sdk.model;

/**
 * A connected account, flagged with whether comments and DMs can be read for it yet.
 * {@code canStartConversation} means a new DM can be opened from it by handle.
 * {@code reconnectRequired} means the grant predates a permission the inbox read needs, so the
 * account is not polled until someone reconnects it.
 */
public record InboxAccount(
        String id,
        String workspaceId,
        String platform,
        String username,
        String name,
        String avatar,
        Boolean inboxSupported,
        String pendingReason,
        Boolean dmSupported,
        String dmPendingReason,
        Boolean canStartConversation,
        Boolean reconnectRequired) {}
