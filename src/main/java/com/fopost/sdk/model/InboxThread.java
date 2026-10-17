package com.fopost.sdk.model;

import java.time.Instant;

/** One platform post and the comments it has collected, or one review left on the business. */
public record InboxThread(
        String workspaceId,
        String accountId,
        String postExternalId,
        Integer commentCount,
        Integer unreadCount,
        Instant lastCommentAt,
        String lastCommentText,
        String lastCommentAuthor,
        Integer rating,
        InboxPostContext post,
        InboxAccountRef account) {}
