package com.fopost.sdk.model;

import java.time.Instant;

/** One platform post and the comments it has collected. */
public record InboxThread(
        String workspaceId,
        String accountId,
        String postExternalId,
        Integer commentCount,
        Integer unreadCount,
        Instant lastCommentAt,
        String lastCommentText,
        String lastCommentAuthor,
        InboxPostContext post,
        InboxAccountRef account) {}
