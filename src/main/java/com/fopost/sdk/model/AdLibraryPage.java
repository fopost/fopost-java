package com.fopost.sdk.model;

/** One page of archive results; pass {@code nextCursor} back as {@code after}. */
public record AdLibraryPage(
        java.util.List<AdLibraryEntry> entries,
        String nextCursor) {}
