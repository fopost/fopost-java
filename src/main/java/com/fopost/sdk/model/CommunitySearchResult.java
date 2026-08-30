package com.fopost.sdk.model;

/** A community as returned by a live search against X. */
public record CommunitySearchResult(String id, String name, String description, Long memberCount) {}
