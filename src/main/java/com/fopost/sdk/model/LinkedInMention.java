package com.fopost.sdk.model;

/** An entity a post can mention; {@code annotation} is what the post text carries. */
public record LinkedInMention(
        String urn, String name, String vanityName, String logoUrl, String type, String annotation) {}
