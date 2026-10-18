package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class AutomationVersionsTest {

    @Test
    void versionsListReadsThePageAndItsMeta() {
        FakeTransport transport = new FakeTransport().enqueue(200, """
                {"data":[{"version":3,"note":"Added delay","created_by":{"name":"Ops"},
                          "created_at":"2026-10-18T10:00:00Z","node_count":4,
                          "nodes_added":[{"id":"n4","kind":"delay","label":null}],"nodes_removed":[]}],
                 "meta":{"page":1,"per_page":20,"total":3,"total_pages":1}}""");

        var page = TestSupport.client(transport).automations().versions("m1", 1, 20);

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/automations/m1/versions?page=1&per_page=20", transport.last().url());
        assertEquals(1, page.size());
        assertEquals(3, page.data().get(0).version());
        assertEquals("Ops", page.data().get(0).createdBy().name());
        assertEquals("delay", page.data().get(0).nodesAdded().get(0).kind());
        assertNull(page.data().get(0).graph());
        assertEquals(1, page.meta().totalPages());
    }

    @Test
    void oneVersionCarriesItsGraphAndRestoreMakesANewOne() {
        String graph = "{\"nodes\":[{\"id\":\"n1\",\"kind\":\"trigger\",\"label\":\"Start\",\"config\":{\"event\":\"post.published\"},"
                + "\"position\":0,\"layout\":{\"x\":10,\"y\":20}}],\"edges\":[{\"id\":\"e1\",\"source\":\"n1\",\"source_handle\":null,\"target\":\"n2\"}]}";
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"version\":2,\"note\":null,\"created_by\":null,\"created_at\":\"2026-10-17T10:00:00Z\",\"graph\":" + graph + "}")
                .enqueue(201, "{\"version\":4,\"graph\":" + graph + ",\"steps\":[{\"position\":1,\"node_id\":\"n2\",\"action_type\":\"publish\",\"action_config\":{}}]}");
        FoPost client = TestSupport.client(transport);

        var version = client.automations().version("m1", 2);
        assertEquals("https://api.fopost.test/v1/automations/m1/versions/2", transport.last().url());
        assertEquals("trigger", version.graph().nodes().get(0).kind());
        assertEquals(10.0, version.graph().nodes().get(0).layout().x());
        assertEquals("n2", version.graph().edges().get(0).target());

        var restored = client.automations().restoreVersion("m1", 2);
        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/automations/m1/versions/2/restore", transport.last().url());
        assertEquals(4, restored.version());
        assertEquals("publish", restored.steps().get(0).actionType());
        assertEquals("n2", restored.steps().get(0).nodeId());
    }
}
