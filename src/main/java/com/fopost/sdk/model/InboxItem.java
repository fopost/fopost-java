package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * A comment, mention or direct message on a connected account.
 *
 * <p>{@code type} is comment, mention or dm; {@code state} is unread, read, resolved or snoozed.
 * {@code canReply}, {@code canHide} and {@code canDelete} say which actions the platform allows on
 * it.
 */
public record InboxItem(
        String id,
        String workspaceId,
        String platform,
        String type,
        String state,
        String direction,
        String conversationId,
        String authorName,
        String authorHandle,
        String authorAvatarUrl,
        String text,
        List<InboxAttachment> attachments,
        String permalink,
        String postExternalId,
        String parentExternalId,
        Instant platformCreatedAt,
        Instant snoozedUntil,
        Instant repliedAt,
        Instant createdAt,
        Boolean canReply,
        Boolean hidden,
        Boolean canHide,
        Boolean canDelete,
        PostRef post,
        InboxPostContext postContext,
        InboxAccountRef account) {}
