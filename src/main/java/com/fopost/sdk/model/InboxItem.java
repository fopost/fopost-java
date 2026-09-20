package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * A comment, mention, review or direct message on a connected account.
 *
 * <p>{@code type} is comment, mention, review or dm; {@code state} is unread, read, resolved or
 * snoozed. {@code rating} is the stars on a review, 1-5, and null on every other type.
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
        Integer rating,
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
        Boolean pinned,
        String reaction,
        Instant editedAt,
        Boolean canLike,
        Boolean canPin,
        Boolean canEdit,
        Boolean canReact,
        Boolean canSendMedia,
        Boolean canQuickReply,
        Boolean canPrivateReply,
        PostRef post,
        InboxPostContext postContext,
        InboxAccountRef account) {}
