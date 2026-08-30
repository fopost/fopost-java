package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.internal.HttpRequestData;
import org.junit.jupiter.api.Test;

class ClientTest {

    @Test
    void requiresAnApiKey() {
        IllegalArgumentException error =
                assertThrows(IllegalArgumentException.class, () -> FoPost.builder().apiKey("").build());
        assertTrue(error.getMessage().contains("FOPOST_API_KEY"));
    }

    @Test
    void sendsTheKeyAndVersionedPath() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":[]}");
        TestSupport.client(transport).workspaces().list();

        HttpRequestData request = transport.last();
        assertEquals("https://api.fopost.test/v1/workspaces", request.url());
        assertEquals("fp_test", request.headers().get("X-API-Key"));
        assertEquals("fopost-java/" + FoPost.VERSION, request.headers().get("User-Agent"));
    }

    @Test
    void trimsTrailingSlashesFromTheBaseUrl() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":[]}");
        FoPost client = FoPost.builder()
                .apiKey("fp_test")
                .baseUrl("http://localhost:8080///")
                .transport(transport)
                .build();

        client.workspaces().list();
        assertEquals("http://localhost:8080/v1/workspaces", transport.last().url());
        assertEquals("http://localhost:8080", client.baseUrl());
    }

    @Test
    void encodesQueryParametersAndSkipsUnsetOnes() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":[],\"meta\":{}}");
        TestSupport.client(transport)
                .posts()
                .list(com.fopost.sdk.param.PostListParams.create().workspaceId("ws 1").status("draft"));

        assertEquals(
                "https://api.fopost.test/v1/posts?workspace_id=ws+1&status=draft", transport.last().url());
    }

    @Test
    void escapeHatchSendsAnAuthenticatedRequest() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"ok\":true}");
        var body = TestSupport.client(transport).request("GET", "/v1/anything", null, java.util.Map.of("days", 30));

        assertEquals("https://api.fopost.test/v1/anything?days=30", transport.last().url());
        assertTrue(body.path("ok").asBoolean());
    }
}
