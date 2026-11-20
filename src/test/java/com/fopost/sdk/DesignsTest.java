package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.param.DesignParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Designs: paths, verbs, snake_case bodies, the document passed through verbatim, and the records. */
class DesignsTest {

    private static final String DESIGN = "{\"id\":\"des_1\",\"name\":\"Launch card\",\"kind\":\"post\","
            + "\"width\":1080,\"height\":1080,\"document\":{\"version\":1,\"pages\":[{\"layers\":[]}]},"
            + "\"thumbnail_media_id\":\"med_9\",\"thumbnail_url\":\"https://api.fopost.test/v1/media/med_9/file\","
            + "\"created_at\":\"2026-11-19T10:00:00.000Z\",\"updated_at\":\"2026-11-19T11:00:00.000Z\"}";

    private static final String VERSION =
            "{\"id\":\"ver_1\",\"version\":3,\"created_at\":\"2026-11-19T11:00:00.000Z\"}";

    @Test
    void listSendsTheFiltersAndReadsDesigns() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":[" + DESIGN + "]}");

        var designs = TestSupport.client(transport)
                .designs()
                .list(DesignParams.Filter.of("w1").kind("story").limit(10).offset(20));

        assertEquals("GET", transport.last().method());
        assertEquals(
                "https://api.fopost.test/v1/designs?workspace_id=w1&kind=story&limit=10&offset=20",
                transport.last().url());
        assertEquals(1, designs.size());
        assertEquals("des_1", designs.get(0).id());
        assertEquals(1080, designs.get(0).width());
        assertEquals("med_9", designs.get(0).thumbnailMediaId());
    }

    @Test
    void createSendsTheDocumentVerbatimWithItsNulls() {
        FakeTransport transport = new FakeTransport().enqueue(201, "{\"data\":" + DESIGN + "}");
        Map<String, Object> layer = new LinkedHashMap<>();
        layer.put("id", "l1");
        layer.put("mediaId", null);
        Map<String, Object> document = Map.of("pages", List.of(Map.of("layers", List.of(layer))));

        var design = TestSupport.client(transport)
                .designs()
                .create(DesignParams.Create.of("w1", "post", document).name("Launch card").brandVoiceId("bv_1"));

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/designs", transport.last().url());
        assertEquals(
                "{\"workspace_id\":\"w1\",\"kind\":\"post\","
                        + "\"document\":{\"pages\":[{\"layers\":[{\"id\":\"l1\",\"mediaId\":null}]}]},"
                        + "\"name\":\"Launch card\",\"brand_voice_id\":\"bv_1\"}",
                transport.lastBody());
        assertEquals("Launch card", design.name());
        assertEquals(1, design.document().get("version"));
    }

    @Test
    void getReadsTheDesign() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":" + DESIGN + "}");

        var design = TestSupport.client(transport).designs().get("des_1");

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/designs/des_1", transport.last().url());
        assertEquals("post", design.kind());
    }

    @Test
    void updateSendsOnlyWhatWasSetAndReadsTheVersion() {
        FakeTransport transport =
                new FakeTransport().enqueue(200, "{\"data\":" + DESIGN + ",\"version\":" + VERSION + "}");

        var saved = TestSupport.client(transport)
                .designs()
                .update("des_1", DesignParams.Update.create().name("Renamed").createVersion(true));

        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/designs/des_1", transport.last().url());
        assertEquals("{\"name\":\"Renamed\",\"create_version\":true}", transport.lastBody());
        assertEquals("des_1", saved.design().id());
        assertEquals(3, saved.version().version());
    }

    @Test
    void updateWithoutAVersionLeavesItNull() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":" + DESIGN + ",\"version\":null}");

        var saved = TestSupport.client(transport).designs().update("des_1", DesignParams.Update.create().kind("story"));

        assertNull(saved.version());
    }

    @Test
    void deleteAnswersTheDeletedId() {
        FakeTransport transport =
                new FakeTransport().enqueue(200, "{\"data\":{\"id\":\"des_1\",\"deleted\":true}}");

        var deleted = TestSupport.client(transport).designs().delete("des_1");

        assertEquals("DELETE", transport.last().method());
        assertEquals("https://api.fopost.test/v1/designs/des_1", transport.last().url());
        assertTrue(deleted.deleted());
    }

    @Test
    void versionsAsksForDocumentsOnlyWhenTold() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[" + VERSION + "]}")
                .enqueue(200, "{\"data\":[{\"id\":\"ver_1\",\"version\":3,\"document\":{\"pages\":[]},"
                        + "\"created_at\":\"2026-11-19T11:00:00.000Z\"}]}");
        FoPost client = TestSupport.client(transport);

        var plain = client.designs().versions("des_1");
        assertEquals("https://api.fopost.test/v1/designs/des_1/versions", transport.last().url());
        assertNull(plain.get(0).document());

        var full = client.designs().versions("des_1", true);
        assertEquals("https://api.fopost.test/v1/designs/des_1/versions?include=document", transport.last().url());
        assertEquals(List.of(), full.get(0).document().get("pages"));
    }

    @Test
    void restoreVersionPostsToTheRestorePath() {
        FakeTransport transport =
                new FakeTransport().enqueue(200, "{\"data\":" + DESIGN + ",\"version\":" + VERSION + "}");

        var saved = TestSupport.client(transport).designs().restoreVersion("des_1", "ver_1");

        assertEquals("POST", transport.last().method());
        assertEquals(
                "https://api.fopost.test/v1/designs/des_1/versions/ver_1/restore", transport.last().url());
        assertEquals("ver_1", saved.version().id());
    }

    @Test
    void duplicateSendsTheNameOnlyWhenGiven() {
        FakeTransport transport = new FakeTransport()
                .enqueue(201, "{\"data\":" + DESIGN + "}")
                .enqueue(201, "{\"data\":" + DESIGN + "}");
        FoPost client = TestSupport.client(transport);

        client.designs().duplicate("des_1");
        assertEquals("https://api.fopost.test/v1/designs/des_1/duplicate", transport.last().url());
        assertEquals("{}", transport.lastBody());

        client.designs().duplicate("des_1", "Copy B");
        assertEquals("{\"name\":\"Copy B\"}", transport.lastBody());
    }

    @Test
    void exportSendsFormatPagesAndDurationAndReadsTheFiles() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"format\":\"mp4\",\"files\":[{\"media_id\":\"med_1\","
                        + "\"page_index\":0,\"mime_type\":\"video/mp4\","
                        + "\"preview_url\":\"https://api.fopost.test/v1/media/med_1/file\"}]}}");

        var export = TestSupport.client(transport)
                .designs()
                .export("des_1", DesignParams.Export.of("mp4").pageIndexes(List.of(0)).durationSec(10));

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/designs/des_1/export", transport.last().url());
        assertEquals("{\"format\":\"mp4\",\"page_indexes\":[0],\"duration_sec\":10}", transport.lastBody());
        assertEquals("mp4", export.format());
        assertEquals("med_1", export.files().get(0).mediaId());
        assertEquals(0, export.files().get(0).pageIndex());
        assertEquals("video/mp4", export.files().get(0).mimeType());
    }

    @Test
    void bulkSendsTheRowsAndTheNameColumn() {
        FakeTransport transport = new FakeTransport().enqueue(201, "{\"data\":[" + DESIGN + "]}");

        var created = TestSupport.client(transport)
                .designs()
                .bulk("des_1", List.of(Map.of("headline", "Hello")), "headline");

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/designs/des_1/bulk", transport.last().url());
        assertEquals("{\"rows\":[{\"headline\":\"Hello\"}],\"name_column\":\"headline\"}", transport.lastBody());
        assertEquals(1, created.size());
    }

    @Test
    void postsReadsThePostsUsingTheDesign() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[{\"id\":\"post_1\",\"status\":\"published\",\"title\":null,"
                        + "\"text\":\"Launch day\",\"schedule_at\":null,"
                        + "\"published_at\":\"2026-11-19T12:00:00.000Z\",\"platforms\":[\"instagram\"],"
                        + "\"engagement\":42,\"views\":null}]}");

        var posts = TestSupport.client(transport).designs().posts("des_1");

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/designs/des_1/posts", transport.last().url());
        assertEquals("post_1", posts.get(0).id());
        assertEquals(List.of("instagram"), posts.get(0).platforms());
        assertEquals(42L, posts.get(0).engagement());
        assertNull(posts.get(0).views());
    }

    @Test
    void textToSlidesSendsTheBodyAndReadsTheSlides() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"cover\":{\"title\":\"Five tips\",\"subtitle\":\"\"},"
                        + "\"slides\":[{\"heading\":\"One\",\"body\":\"First\"}],"
                        + "\"cta\":{\"heading\":\"Follow\",\"body\":\"More soon\"},"
                        + "\"brand\":{\"colors\":[\"#111111\"],\"fonts\":{\"heading\":\"Geist\",\"body\":\"Geist\"},"
                        + "\"logo_media_id\":null}}}");

        var slides = TestSupport.client(transport)
                .designs()
                .textToSlides(DesignParams.TextToSlides.of("w1").text("Five tips for launch").maxSlides(5));

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/designs/text-to-slides", transport.last().url());
        assertEquals(
                "{\"workspace_id\":\"w1\",\"text\":\"Five tips for launch\",\"max_slides\":5}",
                transport.lastBody());
        assertEquals("Five tips", slides.cover().title());
        assertEquals("One", slides.slides().get(0).heading());
        assertEquals("Follow", slides.cta().heading());
        assertEquals("Geist", slides.brand().fonts().heading());
    }

    @Test
    void fromPostSendsTheBodyAndReadsTheDesign() {
        FakeTransport transport = new FakeTransport().enqueue(201, "{\"data\":" + DESIGN + "}");

        var design = TestSupport.client(transport)
                .designs()
                .fromPost(DesignParams.FromPost.of("w1").postId("post_1").style("post").accountId("acc_1")
                        .carousel(false));

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/designs/from-post", transport.last().url());
        String body = transport.lastBody();
        assertEquals(
                "{\"workspace_id\":\"w1\",\"post_id\":\"post_1\",\"style\":\"post\",\"account_id\":\"acc_1\","
                        + "\"carousel\":false}",
                body);
        assertFalse(body.contains("\"text\""), body);
        assertEquals("des_1", design.id());
    }
}
