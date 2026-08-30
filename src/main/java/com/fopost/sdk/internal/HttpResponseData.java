package com.fopost.sdk.internal;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

/** One inbound HTTP response. Header lookup is case-insensitive. */
public record HttpResponseData(int status, Map<String, String> headers, byte[] body) {

    public String header(String name) {
        if (headers == null) {
            return null;
        }
        String wanted = name.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey() != null && entry.getKey().toLowerCase(Locale.ROOT).equals(wanted)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public String bodyAsString() {
        return body == null ? "" : new String(body, StandardCharsets.UTF_8);
    }
}
