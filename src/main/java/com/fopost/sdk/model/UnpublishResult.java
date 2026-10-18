package com.fopost.sdk.model;

import java.util.List;

/**
 * The answer to an unpublish: the post's new status and one entry per network it was live on.
 * {@code removed} is false where the network refused to take the post down.
 */
public record UnpublishResult(String postStatus, List<Removal> deliveries) {

    public record Removal(String accountId, String platform, Boolean removed) {}
}
