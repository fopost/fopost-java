package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.ActivityPage;
import com.fopost.sdk.param.ActivityParams;
import org.junit.jupiter.api.Test;

class ActivityTest {

    private static final String SECURITY_EVENT =
            "{\"data\":[{\"id\":\"evt_1\",\"workspace_id\":\"w1\",\"kind\":\"security\","
                    + "\"ref_type\":\"member_removed\",\"ref_id\":\"u2\","
                    + "\"summary\":\"Removed sam@example.com\","
                    + "\"actor\":{\"type\":\"user\",\"name\":\"Ada\"},"
                    + "\"time\":\"2026-09-20T10:00:00Z\"}],\"meta\":{\"next_cursor\":\"42\"}}";

    @Test
    void readsTheAuditLogAndKeepsTheCursor() {
        FakeTransport transport = new FakeTransport().enqueue(200, SECURITY_EVENT);

        ActivityPage page = TestSupport.client(transport)
                .activity()
                .list(ActivityParams.create()
                        .workspaceId("w1")
                        .kind(ActivityParams.KIND_SECURITY)
                        .limit(1));

        assertTrue(transport.last().url().contains("kind=security"));
        assertTrue(transport.last().url().contains("workspace_id=w1"));
        assertEquals(1, page.size());
        assertEquals("member_removed", page.data().get(0).refType());
        assertEquals("Ada", page.data().get(0).actor().name());
        assertEquals("42", page.nextCursor());
    }

    @Test
    void theEndOfTheListIsANullCursor() {
        FakeTransport transport =
                new FakeTransport().enqueue(200, "{\"data\":[],\"meta\":{\"next_cursor\":null}}");

        ActivityPage page = TestSupport.client(transport).activity().list();

        assertTrue(page.isEmpty());
        assertNull(page.nextCursor());
    }
}
