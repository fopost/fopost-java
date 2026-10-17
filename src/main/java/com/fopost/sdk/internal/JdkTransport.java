package com.fopost.sdk.internal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/** Default transport, on the JDK's own HTTP client. No third-party HTTP dependency. */
public final class JdkTransport implements Transport {

    private final HttpClient client;
    private final Duration timeout;

    public JdkTransport(Duration timeout) {
        this(HttpClient.newBuilder().connectTimeout(timeout).build(), timeout);
    }

    public JdkTransport(HttpClient client, Duration timeout) {
        this.client = client;
        this.timeout = timeout;
    }

    @Override
    public HttpResponseData send(HttpRequestData request) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(request.url()))
                .timeout(timeout)
                .method(request.method(), publisher(request));
        request.headers().forEach(builder::header);

        HttpResponse<byte[]> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());

        Map<String, String> headers = new HashMap<>();
        response.headers().map().forEach((name, values) -> {
            if (!values.isEmpty()) {
                headers.put(name, values.get(0));
            }
        });
        return new HttpResponseData(response.statusCode(), headers, response.body());
    }

    private static HttpRequest.BodyPublisher publisher(HttpRequestData request) {
        if (request.body() != null) {
            return HttpRequest.BodyPublishers.ofByteArray(request.body());
        }
        if (request.bodyStream() != null) {
            // Fixed length so the request carries Content-Length, which a presigned PUT requires.
            return HttpRequest.BodyPublishers.fromPublisher(
                    HttpRequest.BodyPublishers.ofInputStream(request.bodyStream()), request.bodyLength());
        }
        return HttpRequest.BodyPublishers.noBody();
    }
}
