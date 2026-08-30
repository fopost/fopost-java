package com.fopost.sdk.internal;

import java.io.IOException;

/**
 * How the SDK reaches the network.
 *
 * <p>The default is {@link JdkTransport}; swap in your own to route through a proxy,
 * an existing HTTP stack, or a fake in tests.
 */
@FunctionalInterface
public interface Transport {
    HttpResponseData send(HttpRequestData request) throws IOException, InterruptedException;
}
