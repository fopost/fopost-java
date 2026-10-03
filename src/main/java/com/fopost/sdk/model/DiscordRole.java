package com.fopost.sdk.model;

/**
 * A role in the connected server. A managed role belongs to an integration and cannot be edited;
 * {@code permissions} is Discord's bitfield as a decimal string.
 */
public record DiscordRole(
        String id,
        String name,
        int color,
        boolean hoist,
        boolean mentionable,
        boolean managed,
        int position,
        String permissions) {}
