package com.fopost.sdk.internal;

import java.util.Map;

/** One outbound HTTP call, as handed to a {@link Transport}. */
public record HttpRequestData(String method, String url, Map<String, String> headers, byte[] body) {}
