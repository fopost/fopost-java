package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One conversion sent back to the network. It needs an email or a click id; the address is hashed
 * inside the API, so the network never receives it and nothing about an event is stored.
 */
public final class ConversionEventParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private ConversionEventParams() {}

    /** {@code happenedAt} is epoch milliseconds. */
    public static ConversionEventParams at(long happenedAt) {
        ConversionEventParams params = new ConversionEventParams();
        params.body.put("happenedAt", happenedAt);
        return params;
    }

    public ConversionEventParams email(String email) {
        body.put("email", email);
        return this;
    }

    /** The network's click id, as the landing page received it. */
    public ConversionEventParams clickId(String clickId) {
        body.put("clickId", clickId);
        return this;
    }

    public ConversionEventParams value(long valueMinor, String currency) {
        body.put("valueMinor", valueMinor);
        body.put("currency", currency);
        return this;
    }

    /** Your own id for the event, so a replay is counted once. */
    public ConversionEventParams eventId(String eventId) {
        body.put("eventId", eventId);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
