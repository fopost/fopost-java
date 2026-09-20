package com.fopost.sdk.model;

/** How many iOS 14 campaigns an ad account may run at once, per app. */
public record IosCampaignLimits(
        Long limit,
        Long used,
        String appId) {}
