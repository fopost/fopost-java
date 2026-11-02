package com.fopost.sdk.model;

import java.util.List;

/** The events a webhook can subscribe to. */
public final class WebhookEvents {

    private WebhookEvents() {}

    public static final String POST_PUBLISHED = "post.published";
    public static final String POST_FAILED = "post.failed";
    public static final String POST_PARTIALLY_FAILED = "post.partially_failed";
    public static final String DELIVERY_PUBLISHED = "delivery.published";
    public static final String DELIVERY_FAILED = "delivery.failed";
    public static final String DELIVERY_DELAYED = "delivery.delayed";
    public static final String ACCOUNT_HEALTH_CHANGED = "account.health_changed";

    public static final List<String> ALL = List.of(
            POST_PUBLISHED, POST_FAILED, POST_PARTIALLY_FAILED, DELIVERY_PUBLISHED,
            DELIVERY_FAILED, DELIVERY_DELAYED, ACCOUNT_HEALTH_CHANGED);
}
