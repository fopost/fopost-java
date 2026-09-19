package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * A comment, mention or direct message on a connected account.
 *
 * <p>{@code type} is comment, mention or dm; {@code state} is unread, read, resolved or snoozed.
 * The {@code can*} flags say which actions the platform allows on it. {@code canDelete} covers a
 * comment someone left or our own reply; {@code canPin} and {@code canEdit} are our own comments
 * only; {@code canPrivateReply} means a DM can be opened from it with
 * {@code StartConversationParams.privateReply}. {@code reaction} is our reaction on a DM.
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
        Boolean liked,
        String vote,
        Boolean pinned,
        String reaction,
        Instant editedAt,
        Boolean canLike,
        Boolean canVote,
        Boolean canPin,
        Boolean canEdit,
        Boolean canReact,
        Boolean canSendMedia,
        Boolean canQuickReply,
        Boolean canPrivateReply,
        PostRef post,
        InboxPostContext postContext,
        InboxAccountRef account) {}
