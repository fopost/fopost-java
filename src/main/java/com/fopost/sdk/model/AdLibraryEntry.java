package com.fopost.sdk.model;

/** One public archive entry. Read live on every search and stored nowhere. */
public record AdLibraryEntry(
        String id,
        String pageId,
        String pageName,
        java.util.List<String> bodies,
        java.util.List<String> titles,
        java.util.List<String> linkUrls,
        String snapshotUrl,
        java.util.List<String> publisherPlatforms,
        String startedAt,
        String endedAt,
        String currency,
        Long spendLower,
        Long spendUpper,
        Long impressionsLower,
        Long impressionsUpper) {}
