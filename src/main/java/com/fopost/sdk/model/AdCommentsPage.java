package com.fopost.sdk.model;

import java.util.List;

/** One page of an ad's comments. Pass {@code nextCursor} back as {@code after}; null means the end. */
public record AdCommentsPage(List<AdComment> comments, String nextCursor) {}
