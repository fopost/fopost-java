package com.fopost.sdk.model;

import java.time.Instant;

/** A drafted reply waiting for a person. {@code id} is what approve and reject take. */
public record InboxApproval(
        Long id, String workspaceId, String source, String reply, Instant createdAt, Item item) {

    /** The inbox item the draft answers. */
    public record Item(
            String id,
            String platform,
            String type,
            String state,
            String authorName,
            String authorHandle,
            String authorAvatarUrl,
            String text,
            String permalink,
            Instant platformCreatedAt) {}
}
