package com.fopost.sdk.model;

/** The nickname and avatar the bot wears in the server; null means its own. */
public record DiscordIdentity(String username, String avatarUrl) {}
