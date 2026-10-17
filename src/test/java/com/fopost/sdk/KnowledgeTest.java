package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The knowledge base: the path, the query casing, the snake_case request body,
 * and that a camelCase response reads back into the record.
 */
class KnowledgeTest {

    private static final String SOURCE = "{\"id\":\"know_1\",\"kind\":\"url\",\"title\":\"Refund policy\","
            + "\"status\":\"ready\",\"statusMessage\":null,\"url\":\"https://yourbrand.com/help/refunds\","
            + "\"mediaId\":null,\"brandVoiceId\":null,\"chunkCount\":3,\"content\":null,"
            + "\"lastSyncedAt\":\"2026-09-20T00:00:00.000Z\",\"createdAt\":\"2026-09-19T00:00:00.000Z\","
            + "\"updatedAt\":\"2026-09-20T00:00:00.000Z\"}";

    @Test
    void listSendsTheWorkspaceFilterAndReadsCamelCaseFields() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":[" + SOURCE + "]}");

        var sources = TestSupport.client(transport).knowledge().list("w1");

        assertEquals(
                "https://api.fopost.test/v1/knowledge/sources?workspace_id=w1", transport.last().url());
        assertEquals(1, sources.size());
        assertEquals("ready", sources.get(0).status());
        assertEquals(3, sources.get(0).chunkCount());
    }

    @Test
    void createSendsASnakeCaseBodyAndOmitsWhatTheKindDoesNotUse() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":" + SOURCE + "}");

        TestSupport.client(transport).knowledge().createFile("Price list", "media_1");

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/knowledge/sources", transport.last().url());
        String body = transport.lastBody();
        assertTrue(body.contains("\"media_id\":\"media_1\""), body);
        assertTrue(body.contains("\"kind\":\"file\""), body);
        // Nothing the kind does not use reaches the wire.
        assertFalse(body.contains("\"url\""), body);
        assertFalse(body.contains("\"content\""), body);
    }

    @Test
    void updateSendsOnlyTheFieldsThatWereGiven() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":" + SOURCE + "}");

        TestSupport.client(transport).knowledge().update("know_1", "Refunds", null, null, null);

        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/knowledge/sources/know_1", transport.last().url());
        assertEquals("{\"title\":\"Refunds\"}", transport.lastBody());
    }

    @Test
    void searchSendsTopKAndReadsTheMatches() {
        FakeTransport transport = new FakeTransport()
                .enqueue(
                        200,
                        "{\"data\":[{\"sourceId\":\"know_1\",\"sourceTitle\":\"Refund policy\","
                                + "\"sourceKind\":\"url\",\"sourceUrl\":null,"
                                + "\"text\":\"We refund within 30 days.\",\"score\":0.82}]}");

        var matches = TestSupport.client(transport).knowledge().search("refunds", 3, null, null);

        assertTrue(transport.last().url().contains("q=refunds"), transport.last().url());
        assertTrue(transport.last().url().contains("top_k=3"), transport.last().url());
        assertEquals(1, matches.size());
        assertEquals("Refund policy", matches.get(0).sourceTitle());
        assertEquals(0.82, matches.get(0).score(), 0.0001);
    }

    @Test
    void syncPostsToTheSourcesSyncPath() {
        FakeTransport transport =
                new FakeTransport().enqueue(200, "{\"data\":{\"id\":\"know_1\",\"status\":\"pending\"}}");

        TestSupport.client(transport).knowledge().sync("know_1");

        assertEquals("POST", transport.last().method());
        assertEquals(
                "https://api.fopost.test/v1/knowledge/sources/know_1/sync", transport.last().url());
    }
}
