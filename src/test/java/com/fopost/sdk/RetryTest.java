package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RetryTest {

    @Test
    void retriesA429AndReturnsTheFollowingSuccess() {
        FakeTransport transport = new FakeTransport()
                .enqueue(429, "{\"error\":\"too_many_requests\"}", Map.of("retry-after", "2"))
                .enqueue(200, "{\"data\":[{\"id\":\"w1\",\"name\":\"Studio\"}]}");
        List<Duration> sleeps = TestSupport.recorder();

        var workspaces = TestSupport.client(transport, 3, sleeps).workspaces().list();

        assertEquals(1, workspaces.size());
        assertEquals(2, transport.callCount());
        assertEquals(List.of(Duration.ofSeconds(2)), sleeps);
    }

    @Test
    void givesUpAfterMaxRetriesAndReportsTheWait() {
        FakeTransport transport = new FakeTransport()
                .enqueue(429, "{\"error\":\"too_many_requests\"}", Map.of("retry-after", "5"))
                .enqueue(429, "{\"error\":\"too_many_requests\",\"message\":\"Slow down\"}",
                        Map.of("retry-after", "5"));
        List<Duration> sleeps = TestSupport.recorder();

        RateLimitException error = assertThrows(
                RateLimitException.class, () -> TestSupport.client(transport, 2, sleeps).workspaces().list());

        assertEquals(2, transport.callCount());
        assertEquals(1, sleeps.size());
        assertEquals(Duration.ofSeconds(5), error.retryAfter());
        assertEquals("Slow down", error.getMessage());
    }

    @Test
    void capsTheWaitTheApiAsksFor() {
        FakeTransport transport = new FakeTransport()
                .enqueue(429, "{}", Map.of("retry-after", "3600"))
                .enqueue(200, "{\"data\":[]}");
        List<Duration> sleeps = TestSupport.recorder();

        TestSupport.client(transport, 2, sleeps).workspaces().list();

        assertEquals(List.of(Duration.ofSeconds(60)), sleeps);
    }

    @Test
    void doesNotRetryAnythingButA429() {
        FakeTransport transport = new FakeTransport().enqueue(500, "{\"error\":\"server_error\"}");

        assertThrows(FoPostException.class, () -> TestSupport.client(transport).workspaces().list());

        assertEquals(1, transport.callCount());
    }
}
