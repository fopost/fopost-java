package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.param.LogParams;
import org.junit.jupiter.api.Test;

class LogsTest {

    @Test
    void listSendsTheFiltersAndReadsTheCursorAndSummary() {
        FakeTransport transport = new FakeTransport().enqueue(200, """
                {"data":[{"id":91,"workspace_id":"w1","time":"2026-10-18T10:00:00Z","auth_method":"api_key",
                          "method":"POST","route":"/v1/posts","path":"/v1/posts","status_code":422,
                          "duration_ms":12,"platform":null,"message":"content is required"}],
                 "meta":{"next_cursor":"90","has_more":true},
                 "summary":{"since":"2026-10-11T10:00:00Z","total":40,"errors":3,
                            "buckets":[{"at":"2026-10-18T10:00:00Z","total":5,"errors":1}]}}""");

        var page = TestSupport.client(transport)
                .logs()
                .list(LogParams.create().workspaceId("w1").days(7).status("failed").limit(50).before("100"));

        assertEquals("GET", transport.last().method());
        assertEquals(
                "https://api.fopost.test/v1/logs?workspace_id=w1&days=7&status=failed&limit=50&before=100",
                transport.last().url());
        assertEquals(91L, page.data().get(0).id());
        assertEquals(422, page.data().get(0).statusCode());
        assertEquals("content is required", page.data().get(0).message());
        assertEquals("90", page.nextCursor());
        assertTrue(page.hasMore());
        assertEquals(3, page.summary().errors());
        assertEquals(5, page.summary().buckets().get(0).total());
    }

    @Test
    void theEndOfTheLogIsANullCursor() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[],\"meta\":{\"next_cursor\":null,\"has_more\":false},\"summary\":{\"total\":0,\"errors\":0,\"buckets\":[]}}");

        var page = TestSupport.client(transport).logs().list();

        assertEquals("https://api.fopost.test/v1/logs", transport.last().url());
        assertTrue(page.isEmpty());
        assertNull(page.nextCursor());
    }
}
