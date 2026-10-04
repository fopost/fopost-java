package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** What the on-demand refresh of one post managed, per delivery. */
public record CollectPostResult(Integer collected, List<Delivery> deliveries) {

    /** {@code message} says why a refresh did not happen. */
    public record Delivery(
            String accountId,
            String platform,
            String externalPostId,
            Boolean collected,
            Instant fetchedAt,
            String message) {}
}
