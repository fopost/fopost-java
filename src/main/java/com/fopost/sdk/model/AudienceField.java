package com.fopost.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * One custom-field clause in an audience filter.
 *
 * <p>{@code op} is {@code is}, {@code is_not}, {@code contains}, {@code is_set} or
 * {@code is_not_set}; null means {@code is}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AudienceField(String key, String op, String value) {

    public static AudienceField is(String key, String value) {
        return new AudienceField(key, "is", value);
    }

    public static AudienceField isNot(String key, String value) {
        return new AudienceField(key, "is_not", value);
    }

    public static AudienceField contains(String key, String value) {
        return new AudienceField(key, "contains", value);
    }

    public static AudienceField isSet(String key) {
        return new AudienceField(key, "is_set", null);
    }

    public static AudienceField isNotSet(String key) {
        return new AudienceField(key, "is_not_set", null);
    }
}
