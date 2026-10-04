package com.fopost.sdk.model;

/** Where an asset is attached. An asset with no links serves nowhere. */
public record GoogleAssetLink(
        String id, String assetId, String level, String ownerId, String fieldType, String status) {}
