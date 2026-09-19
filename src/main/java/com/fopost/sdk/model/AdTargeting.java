package com.fopost.sdk.model;

import java.util.List;

/** Who an ad is shown to. {@code countries} are ISO 3166-1 alpha-2; {@code gender} is all, male or female. */
public record AdTargeting(
        List<String> countries,
        Integer ageMin,
        Integer ageMax,
        String gender,
        List<String> audienceIds,
        List<Location> locations,
        List<Item> interests,
        List<Item> behaviors,
        List<Item> income) {

    /** {@code type} is region, city, zip or geo_market. */
    public record Location(String key, String name, String type) {}

    public record Item(String id, String name) {}
}
