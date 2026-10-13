package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.HttpRequestData;
import com.fopost.sdk.model.Post;
import com.fopost.sdk.model.UploadLimits;
import com.fopost.sdk.param.CreatePostParams;
import com.fopost.sdk.param.FileInput;
import com.fopost.sdk.param.PresignUploadParams;
import com.fopost.sdk.param.UpdatePostParams;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class UploadLimitsTest {

    private static final String LIMITS = """
            {"data":{"maxFileBytes":2147483648,"source":"accounts","connectedAccounts":9,
                     "largeUploadsEnabled":false,"nextTier":{"upToAccounts":null,"maxFileBytes":5368709120},
                     "multipartMaxFileBytes":104857600,"maxFilesPerUpload":5,"directUploadAvailable":true,
                     "allowedTypes":["image/png","video/mp4"]}}""";

    @Test
    void limitsReadsTheWorkspaceQueryAndTheTypedShape() {
        FakeTransport transport = new FakeTransport().enqueue(200, LIMITS);

        UploadLimits limits = TestSupport.client(transport).media().limits("w1");

        HttpRequestData request = transport.last();
        assertEquals("GET", request.method());
        assertEquals("https://api.fopost.test/v1/media/limits?workspaceId=w1", request.url());
        assertEquals(2147483648L, limits.maxFileBytes());
        assertEquals("accounts", limits.source());
        assertEquals(9, limits.connectedAccounts());
        assertFalse(limits.largeUploadsEnabled());
        assertNull(limits.nextTier().upToAccounts());
        assertEquals(5368709120L, limits.nextTier().maxFileBytes());
        assertEquals(104857600L, limits.multipartMaxFileBytes());
        assertEquals(5, limits.maxFilesPerUpload());
        assertTrue(limits.directUploadAvailable());
        assertEquals(List.of("image/png", "video/mp4"), limits.allowedTypes());
    }

    @Test
    void limitsWithoutAWorkspaceSendsNoQuery() {
        FakeTransport transport = new FakeTransport().enqueue(200, LIMITS);

        TestSupport.client(transport).media().limits(null);

        assertEquals("https://api.fopost.test/v1/media/limits", transport.last().url());
    }

    @Test
    void presignCarriesTheOptionalFieldsAndALongSize() {
        FakeTransport transport = new FakeTransport().enqueue(201, """
                {"data":{"uploadId":"u1","uploadUrl":"https://bucket.test/u1","method":"PUT",
                         "headers":{},"expiresAt":"2026-09-19T12:00:00Z","maxFileBytes":5368709120}}""");

        var presigned = TestSupport.client(transport).media().presign(
                PresignUploadParams.of("w1", "face.png", "image/png", 5368709120L)
                        .folderId("f1")
                        .origin("chat")
                        .brandRole("person")
                        .brandVoiceId("b1")
                        .personConsent("Jane", "Agreed on 2026-09-01"));

        String body = transport.lastBody();
        assertTrue(body.contains("\"size\":5368709120"));
        assertTrue(body.contains("\"folderId\":\"f1\""));
        assertTrue(body.contains("\"origin\":\"chat\""));
        assertTrue(body.contains("\"brandRole\":\"person\""));
        assertTrue(body.contains("\"brandVoiceId\":\"b1\""));
        assertTrue(body.contains("\"personConsent\":{\"subject\":\"Jane\",\"statement\":\"Agreed on 2026-09-01\"}"));
        assertEquals(5368709120L, presigned.maxFileBytes());
    }

    @Test
    void uploadDirectStreamsTheFileWithItsLength() {
        FakeTransport transport = new FakeTransport()
                .enqueue(201, """
                        {"data":{"uploadId":"u1","uploadUrl":"https://bucket.test/u1","method":"PUT",
                                 "headers":{"Content-Type":"video/mp4"},"expiresAt":"2026-09-19T12:00:00Z"}}""")
                .enqueue(200, "", Map.of())
                .enqueue(201, "{\"data\":{\"id\":\"m1\",\"type\":\"video\",\"name\":\"a.mp4\","
                        + "\"url\":\"https://cdn.test/a.mp4\",\"size\":4}}");
        byte[] bytes = {9, 8, 7, 6};

        TestSupport.client(transport).media().uploadDirect(
                "w1", FileInput.of("a.mp4", "video/mp4", new ByteArrayInputStream(bytes), 4));

        HttpRequestData put = transport.requests.get(1);
        assertNull(put.body());
        assertEquals(4L, put.bodyLength());
        assertEquals(List.of(9, 8, 7, 6), bytesOf(FakeTransport.bodyOf(put)));
    }

    @Test
    void createWithSmallFilesSendsOneMultipartRequest() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, LIMITS)
                .enqueue(201, "{\"id\":\"p1\",\"status\":\"draft\"}");

        Post post = TestSupport.client(transport).posts().create(
                CreatePostParams.of("w1").accounts("a1").content("With a picture"),
                List.of(FileInput.of("chart.png", "image/png", new byte[] {1, 2, 3})));

        assertEquals("p1", post.id());
        assertEquals(2, transport.callCount());
        assertEquals("https://api.fopost.test/v1/media/limits?workspaceId=w1", transport.requests.get(0).url());
        HttpRequestData create = transport.last();
        assertEquals("POST", create.method());
        assertEquals("https://api.fopost.test/v1/posts", create.url());
        assertTrue(create.headers().get("Content-Type").startsWith("multipart/form-data; boundary="));
        String body = transport.lastBody();
        assertTrue(body.contains("name=\"post\"\r\n\r\n{\"workspace_id\":\"w1\""));
        assertTrue(body.contains("\"text\":\"With a picture\""));
        assertTrue(body.contains("name=\"files\"; filename=\"chart.png\"\r\nContent-Type: image/png"));
        assertEquals(create.bodyLength(), FakeTransport.bodyOf(create).length);
    }

    @Test
    void createWithALargeFileUploadsDirectlyThenPostsJson() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, LIMITS)
                .enqueue(201, """
                        {"data":{"uploadId":"u1","uploadUrl":"https://bucket.test/u1","method":"PUT",
                                 "headers":{"Content-Type":"video/mp4"},"expiresAt":"2026-09-19T12:00:00Z"}}""")
                .enqueue(200, "", Map.of())
                .enqueue(201, "{\"data\":{\"id\":\"m1\",\"type\":\"video\",\"name\":\"big.mp4\","
                        + "\"url\":\"https://cdn.test/big.mp4\",\"size\":314572800}}")
                .enqueue(201, "{\"id\":\"p1\",\"status\":\"draft\"}");
        FileInput big = FileInput.of("big.mp4", "video/mp4", new ByteArrayInputStream(new byte[0]), 314572800L);

        Post post = TestSupport.client(transport).posts().create(
                CreatePostParams.of("w1").accounts("a1").content("Launch video"), List.of(big));

        assertEquals("p1", post.id());
        assertEquals(5, transport.callCount());
        assertEquals("https://api.fopost.test/v1/media/presign", transport.requests.get(1).url());
        assertEquals("https://bucket.test/u1", transport.requests.get(2).url());
        assertEquals(314572800L, transport.requests.get(2).bodyLength());
        HttpRequestData create = transport.last();
        assertEquals("application/json", create.headers().get("Content-Type"));
        String body = transport.lastBody();
        assertTrue(body.contains("\"content\":[{\"text\":\"Launch video\",\"media\":[{\"type\":\"video\","
                + "\"name\":\"big.mp4\",\"url\":\"https://cdn.test/big.mp4\",\"size\":314572800}]}]"));
    }

    @Test
    void createFallsBackToTheDefaultCeilingWhenLimitsFail() {
        FakeTransport transport = new FakeTransport()
                .enqueue(500, "{\"error\":\"server_error\",\"message\":\"nope\"}")
                .enqueue(201, "{\"id\":\"p1\"}");

        TestSupport.client(transport).posts().create(
                CreatePostParams.of("w1").content("x"),
                List.of(FileInput.of("a.png", "image/png", new byte[] {1})));

        assertEquals(2, transport.callCount());
        assertTrue(transport.last().headers().get("Content-Type").startsWith("multipart/form-data"));
    }

    @Test
    void updateWithALargeFileAndNoContentAppendsToTheExistingFirstBlock() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, LIMITS)
                .enqueue(200, """
                        {"id":"p1","workspace_id":"w1","content":[
                          {"id":"b1","text":"Already here","media":[{"type":"image","name":"old.png",
                           "url":"https://cdn.test/old.png","size":10}]},
                          {"id":"b2","text":"Second block"}]}""")
                .enqueue(201, """
                        {"data":{"uploadId":"u1","uploadUrl":"https://bucket.test/u1","method":"PUT",
                                 "headers":{},"expiresAt":"2026-09-19T12:00:00Z"}}""")
                .enqueue(200, "", Map.of())
                .enqueue(201, "{\"data\":{\"id\":\"m1\",\"type\":\"video\",\"name\":\"big.mp4\","
                        + "\"url\":\"https://cdn.test/big.mp4\",\"size\":200000000}}")
                .enqueue(200, "{\"id\":\"p1\"}");
        FileInput big = FileInput.of("big.mp4", "video/mp4", new ByteArrayInputStream(new byte[0]), 200000000L);

        TestSupport.client(transport).posts().update("p1", UpdatePostParams.create().title("Renamed"), List.of(big));

        assertEquals(6, transport.callCount());
        assertEquals("https://api.fopost.test/v1/media/limits", transport.requests.get(0).url());
        assertEquals("GET", transport.requests.get(1).method());
        assertEquals("https://api.fopost.test/v1/posts/p1", transport.requests.get(1).url());
        assertTrue(transport.requests.get(2).url().endsWith("/v1/media/presign"));
        assertTrue(new String(transport.requests.get(2).body(), StandardCharsets.UTF_8).contains("\"workspaceId\":\"w1\""));
        HttpRequestData update = transport.last();
        assertEquals("PUT", update.method());
        String body = transport.lastBody();
        assertTrue(body.contains("\"title\":\"Renamed\""));
        assertTrue(body.contains("\"content\":[{\"text\":\"Already here\",\"media\":["
                + "{\"type\":\"image\",\"name\":\"old.png\",\"url\":\"https://cdn.test/old.png\",\"size\":10},"
                + "{\"type\":\"video\",\"name\":\"big.mp4\",\"url\":\"https://cdn.test/big.mp4\",\"size\":200000000}]},"
                + "{\"text\":\"Second block\"}]"));
    }

    @Test
    void fileTooLargeSurfacesAs413WithTheCeilingOnTheBody() {
        FakeTransport transport = new FakeTransport().enqueue(413, """
                {"error":"file_too_large","message":"This file is larger than your plan allows",
                 "max_file_bytes":104857600,"file_bytes":314572800,"upgrade_url":"https://app.fopost.test/billing"}""");
        var client = TestSupport.client(transport);

        FoPostException error = assertThrows(
                FoPostException.class,
                () -> client.media().upload("w1", "big.mp4", new byte[] {1}, "video/mp4"));

        assertEquals(413, error.status());
        assertEquals("file_too_large", error.code());
        assertEquals("This file is larger than your plan allows", error.getMessage());
        JsonNode body = (JsonNode) error.body();
        assertEquals(104857600L, body.path("max_file_bytes").asLong());
        assertEquals(314572800L, body.path("file_bytes").asLong());
        assertEquals("https://app.fopost.test/billing", body.path("upgrade_url").asText());
    }

    private static List<Integer> bytesOf(byte[] bytes) {
        Integer[] boxed = new Integer[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            boxed[i] = (int) bytes[i];
        }
        return List.of(boxed);
    }
}
