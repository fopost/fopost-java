package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.param.FileInput;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Fonts: the bare list envelope, the multipart upload fields, and delete. */
class FontsTest {

    private static final String UPLOADED = "{\"id\":\"font_1\",\"family\":\"Brand Sans\",\"style\":\"italic\","
            + "\"weight\":700,\"format\":\"woff2\",\"file_url\":\"https://api.fopost.test/v1/fonts/font_1/file\","
            + "\"created_at\":\"2026-11-19T10:00:00.000Z\"}";

    @Test
    void listReadsTheLibraryAndTheWorkspaceFonts() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"library\":[{\"slug\":\"geist\",\"family\":\"Geist\",\"category\":\"sans\","
                        + "\"scripts\":[\"latin\"],\"weights\":[\"100 900\"],\"files\":[{\"path\":\"/fonts/geist.woff2\","
                        + "\"subset\":\"latin\",\"weight\":\"100 900\",\"style\":\"normal\","
                        + "\"unicode_range\":\"U+0000-00FF\"}]}],\"uploaded\":[" + UPLOADED + "]}");

        var fonts = TestSupport.client(transport).fonts().list("w1");

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/fonts?workspace_id=w1", transport.last().url());
        assertEquals("Geist", fonts.library().get(0).family());
        assertEquals("U+0000-00FF", fonts.library().get(0).files().get(0).unicodeRange());
        assertEquals("font_1", fonts.uploaded().get(0).id());
        assertEquals(700, fonts.uploaded().get(0).weight());
    }

    @Test
    void listWithoutAWorkspaceSendsNoQuery() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"library\":[],\"uploaded\":[]}");

        var fonts = TestSupport.client(transport).fonts().list();

        assertEquals("https://api.fopost.test/v1/fonts", transport.last().url());
        assertTrue(fonts.uploaded().isEmpty());
    }

    @Test
    void uploadSendsTheFileAndTheOptionalFields() {
        FakeTransport transport = new FakeTransport().enqueue(201, UPLOADED);

        var font = TestSupport.client(transport)
                .fonts()
                .upload("w1", FileInput.of("brand.woff2", "font/woff2", new byte[] {1, 2, 3}), "Brand Sans", 700,
                        "italic");

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/fonts", transport.last().url());
        assertTrue(transport.last().headers().get("Content-Type").startsWith("multipart/form-data; boundary="));
        String body = transport.lastBody();
        assertTrue(body.contains("name=\"file\"; filename=\"brand.woff2\""), body);
        assertTrue(body.contains("name=\"workspace_id\"\r\n\r\nw1"), body);
        assertTrue(body.contains("name=\"family\"\r\n\r\nBrand Sans"), body);
        assertTrue(body.contains("name=\"weight\"\r\n\r\n700"), body);
        assertTrue(body.contains("name=\"style\"\r\n\r\nitalic"), body);
        assertEquals("Brand Sans", font.family());
        assertEquals("italic", font.style());
    }

    @Test
    void uploadFromAPathLeavesTheDefaultsToTheServer(@TempDir Path dir) throws Exception {
        Path file = Files.write(dir.resolve("brand.ttf"), new byte[] {0, 1, 0, 0});
        FakeTransport transport = new FakeTransport().enqueue(201, UPLOADED);

        TestSupport.client(transport).fonts().upload("w1", file);

        String body = transport.lastBody();
        assertTrue(body.contains("filename=\"brand.ttf\""), body);
        assertFalse(body.contains("name=\"family\""), body);
        assertFalse(body.contains("name=\"weight\""), body);
    }

    @Test
    void deleteSendsADeleteToTheFont() {
        FakeTransport transport = new FakeTransport().enqueue(204, "");

        TestSupport.client(transport).fonts().delete("font_1");

        assertEquals("DELETE", transport.last().method());
        assertEquals("https://api.fopost.test/v1/fonts/font_1", transport.last().url());
    }
}
