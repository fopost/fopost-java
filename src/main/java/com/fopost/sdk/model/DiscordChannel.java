package com.fopost.sdk.model;

/**
 * A Discord text channel the bot can post to; {@code isCurrent} marks this account's.
 *
 * <p>{@code type} is Discord's channel type: 0 text, 5 announcement, 15 forum.
 */
public record DiscordChannel(
        String id,
        String name,
        int type,
        String parentId,
        boolean nsfw,
        /** False when a channel permission in Discord shuts the bot out. */
        boolean canPost,
        boolean isCurrent) {}
