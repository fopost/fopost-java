package com.fopost.sdk.model;

import java.util.List;

/**
 * The check of one public media url. {@code mimeType} and {@code type} ({@code image},
 * {@code video}, {@code audio} or {@code document}) are present only when {@code ok}.
 */
public record MediaValidation(
        Boolean ok, List<String> issues, String name, Long size, String mimeType, String type) {

    public boolean isOk() {
        return Boolean.TRUE.equals(ok);
    }
}
