package com.fopost.sdk.model;

/** What a Discord delete, pin or role assignment answers. */
public record DiscordAck(Boolean deleted, Boolean pinned, Boolean assigned) {}
