package com.fopost.sdk.model;

import java.util.List;

/**
 * The statuses a post moves through.
 *
 * <p>Only {@link #DRAFT} and {@link #SCHEDULED} may be set by a client; the rest are set by the
 * API as delivery progresses.
 */
public final class PostStatus {

    private PostStatus() {}

    public static final String DRAFT = "draft";
    public static final String SCHEDULED = "scheduled";
    public static final String PUBLISHING = "publishing";
    public static final String PUBLISHED = "published";
    public static final String PARTIALLY_FAILED = "partially_failed";
    public static final String FAILED = "failed";
    public static final String CANCELLED = "cancelled";

    public static final List<String> ALL =
            List.of(DRAFT, SCHEDULED, PUBLISHING, PUBLISHED, PARTIALLY_FAILED, FAILED, CANCELLED);
}
