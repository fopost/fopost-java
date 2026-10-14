package com.fopost.sdk.model;

import java.util.List;

/** Where published articles go: the hosted product blog, a connected account, or your own site. */
public final class BlogTargetKind {

    private BlogTargetKind() {}

    public static final String PRODUCT_BLOG = "product_blog";
    public static final String ACCOUNT = "account";
    public static final String WEBSITE = "website";

    public static final List<String> ALL = List.of(PRODUCT_BLOG, ACCOUNT, WEBSITE);
}
