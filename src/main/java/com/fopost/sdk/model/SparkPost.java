package com.fopost.sdk.model;

/** A post already live on the network, offered as the source of a Spark ad. */
public record SparkPost(
        String id, String identityId, String caption, String thumbnailUrl, String createdAt, Long views) {}
