package com.fopost.sdk.param;

import java.time.Instant;
import java.util.Map;

/** Shared plumbing for the parameter builders: skip what the caller never set. */
final class Params {

    private Params() {}

    static void put(Map<String, Object> into, String key, Object value) {
        if (value != null) {
            into.put(key, value);
        }
    }

    static String iso(Instant value) {
        return value == null ? null : value.toString();
    }
}
