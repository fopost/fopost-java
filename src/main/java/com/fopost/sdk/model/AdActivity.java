package com.fopost.sdk.model;

/** One change recorded on an ad account. */
public record AdActivity(
        String id,
        String eventType,
        String actorName,
        String objectName,
        String objectType,
        String extraData,
        String createdAt) {}
