package com.fopost.sdk.param;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fopost.sdk.internal.Json;

/**
 * The body of the Discord scheduled-event create and update calls. Give {@code channelId} for an
 * event in a voice or stage channel, or {@code location} with an {@code endTime} for one elsewhere.
 * On an update, a field you never set is left as it is.
 */
public final class DiscordEventParams {

    private final ObjectNode body = Json.MAPPER.createObjectNode();

    public static DiscordEventParams create() {
        return new DiscordEventParams();
    }

    public DiscordEventParams name(String name) {
        body.put("name", name);
        return this;
    }

    public DiscordEventParams description(String description) {
        body.put("description", description);
        return this;
    }

    /** RFC 3339. */
    public DiscordEventParams startTime(String startTime) {
        body.put("start_time", startTime);
        return this;
    }

    /** RFC 3339; required for an event at a location. */
    public DiscordEventParams endTime(String endTime) {
        body.put("end_time", endTime);
        return this;
    }

    public DiscordEventParams channelId(String channelId) {
        body.put("channel_id", channelId);
        return this;
    }

    public DiscordEventParams location(String location) {
        body.put("location", location);
        return this;
    }

    /** scheduled, active, completed or canceled; only meaningful on an update. */
    public DiscordEventParams status(String status) {
        body.put("status", status);
        return this;
    }

    public ObjectNode toJson() {
        return body.deepCopy();
    }
}
