package com.fopost.sdk.model;

import java.time.Instant;

/** The platform post an item sits under, whoever published it. {@code isOwn} means one of our accounts did. */
public record InboxPostContext(
        String externalId,
        Boolean isOwn,
        String text,
        String authorName,
        String authorHandle,
        String authorAvatarUrl,
        String thumbnailUrl,
        String permalink,
        Instant publishedAt,
        PostRef published) {}
