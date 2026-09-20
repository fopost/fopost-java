package com.fopost.sdk.model;

/**
 * A comment on an ad, read live from the network and never stored.
 * {@code parentId} is the comment this one answers, when it is not on the ad itself.
 */
public record AdComment(
        String id,
        String adId,
        String text,
        String authorName,
        String authorAvatarUrl,
        String createdAt,
        long likes,
        long replyCount,
        boolean hidden,
        String parentId) {}
