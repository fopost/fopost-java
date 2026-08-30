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
        HttpRequest.BodyPublisher publisher = request.body() == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofByteArray(request.body());

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(request.url()))
                .timeout(timeout)
                .method(request.method(), publisher);
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
}
