package com.fopost.sdk.param;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fopost.sdk.internal.Json;

/** The body of the Discord role create and update calls. */
public final class DiscordRoleParams {

    private final ObjectNode body = Json.MAPPER.createObjectNode();

    public static DiscordRoleParams create() {
        return new DiscordRoleParams();
    }

    public DiscordRoleParams name(String name) {
        body.put("name", name);
        return this;
    }

    /** An RGB integer, e.g. 5793266. */
    public DiscordRoleParams color(int color) {
        body.put("color", color);
        return this;
    }

    /** Show members with this role separately in the member list. */
    public DiscordRoleParams hoist(boolean hoist) {
        body.put("hoist", hoist);
        return this;
    }

    public DiscordRoleParams mentionable(boolean mentionable) {
        body.put("mentionable", mentionable);
        return this;
    }

    /** Discord's permission bitfield as a decimal string. */
    public DiscordRoleParams permissions(String permissions) {
        body.put("permissions", permissions);
        return this;
    }

    public ObjectNode toJson() {
        return body.deepCopy();
    }
}
