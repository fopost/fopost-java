package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.VideoEditOperation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Video edits: the bare job envelope, the operations passed through unchanged, and the queries. */
class VideoEditsTest {

    private static final String JOB = "{\"id\":\"ve_1\",\"status\":\"succeeded\",\"progress\":100,"
            + "\"source_media_id\":\"med_1\",\"operations\":[{\"op\":\"trim\",\"params\":{\"startSec\":2,\"endSec\":12}}],"
            + "\"result_media_id\":\"med_2\",\"result_media\":{\"id\":\"med_2\","
            + "\"preview_url\":\"https://api.fopost.test/v1/media/med_2/file\",\"mime_type\":\"video/mp4\"},"
            + "\"extra_outputs\":[{\"media_id\":\"med_3\",\"label\":\"Subtitles\",\"mime\":\"text/vtt\",\"preview_url\":null}],"
            + "\"result\":{\"language\":\"en\"},\"error\":null,\"created_at\":\"2026-11-19T10:00:00.000Z\","
            + "\"updated_at\":\"2026-11-19T10:01:00.000Z\",\"completed_at\":\"2026-11-19T10:01:00.000Z\"}";

    @Test
    void opsReadsWhatCanRun() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[{\"name\":\"trim\",\"available\":true,\"uses_ai\":false},"
                        + "{\"name\":\"subtitles\",\"available\":false,\"uses_ai\":true}]}");

        var ops = TestSupport.client(transport).videoEdits().ops();

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/video-edits/ops", transport.last().url());
        assertEquals("trim", ops.get(0).name());
        assertTrue(ops.get(0).available());
        assertTrue(ops.get(1).usesAi());
        assertFalse(ops.get(1).available());
    }

    @Test
    void listSendsTheFiltersAndReadsJobs() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":[" + JOB + "]}");

        var jobs = TestSupport.client(transport).videoEdits().list("w1", "med_1", 5);

        assertEquals("GET", transport.last().method());
        assertEquals(
                "https://api.fopost.test/v1/video-edits?workspace_id=w1&media_id=med_1&limit=5",
                transport.last().url());
        assertEquals("ve_1", jobs.get(0).id());
    }

    @Test
    void createSendsTheOperationsVerbatim() {
        FakeTransport transport = new FakeTransport().enqueue(201, JOB);
        Map<String, Object> subtitles = new LinkedHashMap<>();
        subtitles.put("language", null);

        var job = TestSupport.client(transport)
                .videoEdits()
                .create("med_1", List.of(
                        VideoEditOperation.of("trim", Map.of("startSec", 2)),
                        VideoEditOperation.of("subtitles", subtitles),
                        VideoEditOperation.of("denoise")));

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/video-edits", transport.last().url());
        assertEquals(
                "{\"media_id\":\"med_1\",\"operations\":[{\"op\":\"trim\",\"params\":{\"startSec\":2}},"
                        + "{\"op\":\"subtitles\",\"params\":{\"language\":null}},{\"op\":\"denoise\",\"params\":{}}]}",
                transport.lastBody());
        assertEquals("succeeded", job.status());
        assertEquals(2, job.operations().get(0).params().get("startSec"));
    }

    @Test
    void createNamesTheWorkspaceWhenGiven() {
        FakeTransport transport = new FakeTransport().enqueue(201, JOB);

        TestSupport.client(transport).videoEdits().create("w1", "med_1", List.of(VideoEditOperation.of("denoise")));

        assertTrue(transport.lastBody().startsWith("{\"workspace_id\":\"w1\",\"media_id\":\"med_1\""));
    }

    @Test
    void getReadsTheJobAndItsOutputs() {
        FakeTransport transport = new FakeTransport().enqueue(200, JOB);

        var job = TestSupport.client(transport).videoEdits().get("ve_1");

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/video-edits/ve_1", transport.last().url());
        assertEquals(100, job.progress());
        assertEquals("med_2", job.resultMediaId());
        assertEquals("video/mp4", job.resultMedia().mimeType());
        assertEquals("med_3", job.extraOutputs().get(0).mediaId());
        assertNull(job.extraOutputs().get(0).previewUrl());
        assertEquals("en", job.result().get("language"));
        assertNull(job.error());
    }

    @Test
    void getSendsTheWorkspaceWhenGiven() {
        FakeTransport transport = new FakeTransport().enqueue(200, JOB);

        TestSupport.client(transport).videoEdits().get("ve_1", "w1");

        assertEquals("https://api.fopost.test/v1/video-edits/ve_1?workspace_id=w1", transport.last().url());
    }
}
