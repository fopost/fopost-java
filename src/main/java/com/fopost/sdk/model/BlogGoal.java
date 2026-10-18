package com.fopost.sdk.model;

import java.util.List;

/** What the blog is for; steers suggested search terms and keyword ranking. */
public final class BlogGoal {

    private BlogGoal() {}

    public static final String TRAFFIC = "traffic";
    public static final String LEADS = "leads";
    public static final String SALES = "sales";
    public static final String SIGNUPS = "signups";
    public static final String AUTHORITY = "authority";

    public static final List<String> ALL = List.of(TRAFFIC, LEADS, SALES, SIGNUPS, AUTHORITY);
}
