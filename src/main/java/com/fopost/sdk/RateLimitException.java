package com.fopost.sdk;

import java.time.Duration;

/**
 * 429 — the per-key, per-minute rate limit was exceeded.
 *
 * <p>Raised only after the client has exhausted its automatic retries.
 */
public class RateLimitException extends FoPostException {
    private static final long serialVersionUID = 1L;

    private final transient Duration retryAfter;

    public RateLimitException(String message, int status, String code, Object body, Duration retryAfter) {
        super(message, status, code, body);
        this.retryAfter = retryAfter;
    }

    /** How long the API asked the caller to wait, when it sent {@code Retry-After}. */
    public Duration retryAfter() {
        return retryAfter;
    }
}
