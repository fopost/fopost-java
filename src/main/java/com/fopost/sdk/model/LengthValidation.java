package com.fopost.sdk.model;

import java.util.List;

/**
 * How a text measures against each platform's limit. {@code limit} is null when a platform has
 * none; {@code unit} is {@code chars} or {@code bytes}.
 */
public record LengthValidation(Boolean ok, List<PlatformLength> platforms) {

    public record PlatformLength(
            String platform, Integer length, Integer limit, String unit, Boolean ok, List<ContentSignal> signals) {}

    public boolean isOk() {
        return Boolean.TRUE.equals(ok);
    }
}
