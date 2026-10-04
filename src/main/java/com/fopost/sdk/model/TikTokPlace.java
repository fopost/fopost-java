package com.fopost.sdk.model;

/**
 * A place a post can be tagged with. Pass {@code id} as the {@code location_id} platform setting.
 */
public record TikTokPlace(String id, String name, String address, String city, String country) {}
