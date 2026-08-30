package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.Account;
import com.fopost.sdk.model.Post;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * The API is not consistent about wire casing, and adds fields over time. Both have to be
 * survivable for an SDK release to outlive one server deploy.
 */
class ModelsTest {

    @Test
    void readsSnakeCaseAndCamelCaseForTheSameField() {
        FakeTransport snake = new FakeTransport().enqueue(200, "{\"data\":[{\"id\":\"a1\",\"workspace_id\":\"w1\"}]}");
        FakeTransport camel = new FakeTransport().enqueue(200, "{\"data\":[{\"id\":\"a1\",\"workspaceId\":\"w1\"}]}");

        Account fromSnake = TestSupport.client(snake).accounts().list().get(0);
        Account fromCamel = TestSupport.client(camel).accounts().list().get(0);

        assertEquals("w1", fromSnake.workspaceId());
        assertEquals("w1", fromCamel.workspaceId());
    }

    @Test
    void ignoresFieldsTheServerAddsLater() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"id\":\"p1\",\"status\":\"draft\",\"a_field_from_the_future\":{\"nested\":1}}");

        Post post = TestSupport.client(transport).posts().get("p1");

        assertEquals("p1", post.id());
        assertNull(post.title());
    }

    @Test
    void parsesTimestampsAsInstants() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"id\":\"p1\",\"status\":\"scheduled\",\"schedule_at\":\"2026-09-01T10:00:00.000Z\"}");

        Post post = TestSupport.client(transport).posts().get("p1");

        assertEquals(Instant.parse("2026-09-01T10:00:00Z"), post.scheduleAt());
    }

    @Test
    void nullCollectionsStayNullRatherThanBlowingUp() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"id\":\"p1\",\"status\":\"draft\"}");

        Post post = TestSupport.client(transport).posts().get("p1");

        assertNull(post.content());
        assertTrue(post.id().equals("p1"));
    }
}
