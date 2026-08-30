package com.fopost.sdk.internal;

import java.time.Duration;

/** Indirected so tests can drain the retry loop without touching the clock. */
@FunctionalInterface
public interface Sleeper {

    Sleeper DEFAULT = duration -> {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    };

    void sleep(Duration duration);
}
