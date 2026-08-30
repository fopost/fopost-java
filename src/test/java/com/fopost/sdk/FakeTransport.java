package com.fopost.sdk;

import com.fopost.sdk.internal.HttpRequestData;
import com.fopost.sdk.internal.HttpResponseData;
import com.fopost.sdk.internal.Transport;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/** Canned responses and a record of what was sent, so tests never touch the network. */
final class FakeTransport implements Transport {

    final List<HttpRequestData> requests = new ArrayList<>();
    private final Deque<HttpResponseData> responses = new ArrayDeque<>();

    FakeTransport enqueue(int status, String body) {
        return enqueue(status, body, Map.of("content-type", "application/json"));
    }

    FakeTransport enqueue(int status, String body, Map<String, String> headers) {
        responses.add(new HttpResponseData(status, headers, body.getBytes(StandardCharsets.UTF_8)));
        return this;
    }

    @Override
    public HttpResponseData send(HttpRequestData request) {
        requests.add(request);
        HttpResponseData response = responses.poll();
        if (response == null) {
            throw new IllegalStateException("fake transport: no response queued for " + request.url());
        }
        return response;
    }

    HttpRequestData last() {
        if (requests.isEmpty()) {
            throw new IllegalStateException("no request was sent");
        }
        return requests.get(requests.size() - 1);
    }

    String lastBody() {
        byte[] body = last().body();
        return body == null ? "" : new String(body, StandardCharsets.UTF_8);
    }

    int callCount() {
        return requests.size();
    }
}
