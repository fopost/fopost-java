package com.fopost.sdk.param;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fopost.sdk.internal.Json;

/**
 * A partial update to the nickname and avatar a Discord bot connection wears. An unset field keeps
 * its value and {@code null} clears it.
 */
public final class UpdateDiscordIdentityParams {

    // An ObjectNode keeps an explicit null, which the mapper drops from a Map.
    private final ObjectNode body = Json.MAPPER.createObjectNode();

    public static UpdateDiscordIdentityParams create() {
        return new UpdateDiscordIdentityParams();
    }

    /** 1-32 characters, or null for the application's own name. */
    public UpdateDiscordIdentityParams username(String username) {
        body.put("username", username);
        return this;
    }

    /** An http(s) image URL, or null to clear it. */
    public UpdateDiscordIdentityParams avatarUrl(String avatarUrl) {
        body.put("avatar_url", avatarUrl);
        return this;
    }

    public ObjectNode toJson() {
        return body.deepCopy();
    }
}
