package com.fopost.sdk.model;

import java.util.List;

/** The account's assets with the links that place each one. */
public record GoogleAssetsResult(List<GoogleAsset> assets, List<GoogleAssetLink> links) {}
