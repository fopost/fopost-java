package com.fopost.sdk.model;

import java.util.List;

/** The answer to a retry: what was re-queued, and what has run out of attempts. */
public record RetryResult(String postStatus, List<Delivery> deliveries, List<Exceeded> exceeded) {

    public record Exceeded(String accountId, String platform, Integer attempts) {}
}
