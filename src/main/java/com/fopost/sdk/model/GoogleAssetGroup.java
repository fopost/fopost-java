package com.fopost.sdk.model;

import java.util.List;

/** A Performance Max asset group. */
public record GoogleAssetGroup(
        String id, String campaignId, String name, String status, List<String> finalUrls) {}
