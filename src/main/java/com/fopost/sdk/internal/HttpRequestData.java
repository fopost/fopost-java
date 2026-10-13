package com.fopost.sdk.internal;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Supplier;

/**
 * One outbound HTTP call, as handed to a {@link Transport}.
 *
 * <p>The body is either {@code body} (in memory) or {@code bodyStream} with its exact
 * {@code bodyLength}, so a large upload is never read into a byte array. At most one is set.
 */
public record HttpRequestData(
        String method,
        String url,
        Map<String, String> headers,
        byte[] body,
        Supplier<InputStream> bodyStream,
        long bodyLength) {

    public HttpRequestData(String method, String url, Map<String, String> headers, byte[] body) {
        this(method, url, headers, body, null, body == null ? -1 : body.length);
    }

    public boolean hasBody() {
        return body != null || bodyStream != null;
    }
}
