package com.fopost.sdk;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/** Builders shared by the test classes. */
final class TestSupport {

    private TestSupport() {}

    static FoPost client(FakeTransport transport) {
        return FoPost.builder()
                .apiKey("fp_test")
                .baseUrl("https://api.fopost.test")
                .transport(transport)
                .sleeper(duration -> {})
                .build();
    }

    static FoPost client(FakeTransport transport, int maxRetries, List<Duration> sleeps) {
        return FoPost.builder()
                .apiKey("fp_test")
                .baseUrl("https://api.fopost.test")
                .transport(transport)
                .maxRetries(maxRetries)
                .sleeper(sleeps::add)
                .build();
    }

    static List<Duration> recorder() {
        return new ArrayList<>();
    }
}
