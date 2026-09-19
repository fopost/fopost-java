package com.fopost.sdk.param;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fopost.sdk.internal.Json;

/**
 * A partial update to a Slack posting identity. An unset field keeps its value and {@code null} clears
 * it. Set {@code iconUrl} or {@code iconEmoji}, not both; setting one clears the other.
 */
public final class UpdateSlackIdentityParams {

    // An ObjectNode keeps an explicit null, which the mapper drops from a Map.
    private final ObjectNode body = Json.MAPPER.createObjectNode();

    public static UpdateSlackIdentityParams create() {
        return new UpdateSlackIdentityParams();
    }

    /** 1-80 characters, or null for the app name. */
    public UpdateSlackIdentityParams username(String username) {
        body.put("username", username);
        return this;
    }

    /** An http(s) image URL, or null to clear it. */
    public UpdateSlackIdentityParams iconUrl(String iconUrl) {
        body.put("icon_url", iconUrl);
        return this;
    }

    /** An emoji code such as {@code :rocket:}, or null to clear it. */
    public UpdateSlackIdentityParams iconEmoji(String iconEmoji) {
        body.put("icon_emoji", iconEmoji);
        return this;
    }

    public ObjectNode toJson() {
        return body.deepCopy();
    }
}
