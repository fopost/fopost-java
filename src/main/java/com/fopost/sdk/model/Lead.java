package com.fopost.sdk.model;

import java.util.List;

/** One submission of a lead form. */
public record Lead(
        String id,
        String createdAt,
        List<Field> fields,
        String adName,
        String campaignName,
        String platform,
        Boolean isOrganic) {

    public record Field(String name, List<String> values) {}
}
