package com.fopost.sdk.model;

import java.util.List;

/** One page of leads. Pass {@code nextCursor} back as {@code after} for the next; null means the end. */
public record LeadsPage(List<Lead> leads, String nextCursor) {}
