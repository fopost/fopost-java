package com.fopost.sdk.model;

import java.util.List;

/** A person in the connected server; {@code id} is the member id for a DM or a role. */
public record DiscordMember(
        String id,
        String username,
        String displayName,
        String nick,
        String avatar,
        boolean isBot,
        List<String> roles,
        String joinedAt) {}
