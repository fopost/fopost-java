package com.fopost.sdk.model;

import java.util.List;

/** Where a keyword sits in the plan. */
public final class BlogKeywordStatus {

    private BlogKeywordStatus() {}

    public static final String SUGGESTED = "suggested";
    public static final String PLANNED = "planned";
    public static final String WRITTEN = "written";
    public static final String DISMISSED = "dismissed";

    public static final List<String> ALL = List.of(SUGGESTED, PLANNED, WRITTEN, DISMISSED);
}
