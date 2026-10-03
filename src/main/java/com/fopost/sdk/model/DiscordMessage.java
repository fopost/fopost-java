package com.fopost.sdk.model;

/** A message in the connected channel. */
public record DiscordMessage(
        String id,
        String channelId,
        String content,
        String authorId,
        String authorName,
        boolean pinned,
        String createdAt) {}
