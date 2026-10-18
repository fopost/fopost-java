package com.fopost.sdk.model;

import java.util.List;

/** The statuses an article moves through, from the request to the live page. */
public final class BlogArticleStatus {

    private BlogArticleStatus() {}

    public static final String QUEUED = "queued";
    public static final String WRITING = "writing";
    public static final String DRAFT = "draft";
    public static final String IN_REVIEW = "in_review";
    public static final String SCHEDULED = "scheduled";
    public static final String PUBLISHING = "publishing";
    public static final String PUBLISHED = "published";
    public static final String FAILED = "failed";
    public static final String REJECTED = "rejected";

    public static final List<String> ALL =
            List.of(QUEUED, WRITING, DRAFT, IN_REVIEW, SCHEDULED, PUBLISHING, PUBLISHED, FAILED, REJECTED);
}
