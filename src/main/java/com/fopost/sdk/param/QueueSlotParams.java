package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A posting slot to create or change. Creating needs {@code weekday}, {@code time} and
 * {@code timezone}; an update sends only what was set.
 *
 * <pre>{@code
 * QueueSlotParams.create().weekday(1).time("09:30").timezone("Europe/Berlin");
 * }</pre>
 */
public final class QueueSlotParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    public static QueueSlotParams create() {
        return new QueueSlotParams();
    }

    /** Limit the slot to one account; null applies it to every account in the workspace. */
    public QueueSlotParams accountId(String accountId) {
        body.put("account_id", accountId);
        return this;
    }

    /** 0 is Sunday, 6 is Saturday. */
    public QueueSlotParams weekday(int weekday) {
        body.put("weekday", weekday);
        return this;
    }

    /** {@code HH:MM}, 24-hour, in the slot's time zone. */
    public QueueSlotParams time(String time) {
        body.put("time", time);
        return this;
    }

    /** An IANA zone name. */
    public QueueSlotParams timezone(String timezone) {
        body.put("timezone", timezone);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
