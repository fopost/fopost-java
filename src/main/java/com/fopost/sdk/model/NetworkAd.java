package com.fopost.sdk.model;

/** An ad inside an ad set on a Meta ad account, read live. */
public record NetworkAd(
        String id,
        String name,
        String campaignId,
        String adSetId,
        String creativeId,
        String status,
        String effectiveStatus,
        String createdAt) {}
