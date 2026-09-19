package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** A published post with a delivery on an account an ads connection reaches. */
public record BoostablePost(
        String id, String workspaceId, String text, String thumbnailUrl, List<Delivery> deliveries) {

    public record Delivery(
            String accountId, String platform, String username, String externalUrl, Instant postedAt) {}
}
