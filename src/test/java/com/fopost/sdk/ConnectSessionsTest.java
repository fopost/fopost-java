package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fopost.sdk.param.ConnectSessionParams;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConnectSessionsTest {

    private static final String SESSION =
            "{\"id\":\"cs1\",\"status\":\"pending\",\"external_user_id\":\"user_42\",\"platforms\":[\"linkedin\"],"
                    + "\"redirect_url\":\"https://yourbrand.com/connected\",\"branding\":{\"name\":\"Your Brand\",\"logo_url\":null},"
                    + "\"account_id\":null,\"url\":\"https://app.fopost.test/connect/cs1\","
                    + "\"expires_at\":\"2026-10-18T11:00:00Z\",\"used_at\":null,\"created_at\":\"2026-10-18T10:00:00Z\"}";

    @Test
    void createSendsACamelCaseBodyAndReadsTheLink() {
        FakeTransport transport = new FakeTransport().enqueue(201, SESSION);

        var session = TestSupport.client(transport)
                .connectSessions()
                .create(ConnectSessionParams.of("user_42", List.of("linkedin"))
                        .workspaceId("w1")
                        .redirectUrl("https://yourbrand.com/connected")
                        .branding("Your Brand", null));

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/connect-sessions", transport.last().url());
        assertEquals(
                "{\"externalUserId\":\"user_42\",\"platforms\":[\"linkedin\"],\"workspaceId\":\"w1\","
                        + "\"redirectUrl\":\"https://yourbrand.com/connected\",\"branding\":{\"name\":\"Your Brand\"}}",
                transport.lastBody());
        assertEquals("https://app.fopost.test/connect/cs1", session.url());
        assertEquals("Your Brand", session.branding().name());
        assertNull(session.branding().logoUrl());
        assertEquals(Instant.parse("2026-10-18T11:00:00Z"), session.expiresAt());
    }

    @Test
    void getReadsTheSessionById() {
        FakeTransport transport = new FakeTransport().enqueue(200, SESSION);

        var session = TestSupport.client(transport).connectSessions().get("cs1");

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/connect-sessions/cs1", transport.last().url());
        assertEquals("pending", session.status());
        assertEquals("user_42", session.externalUserId());
    }
}
