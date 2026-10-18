package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.param.CreatePostParams;
import com.fopost.sdk.param.QueueSlotParams;
import com.fopost.sdk.param.UpdatePostParams;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class QueueTest {

    private static final String SLOT =
            "{\"id\":\"s1\",\"workspace_id\":\"w1\",\"account_id\":null,\"weekday\":1,\"time\":\"09:30\","
                    + "\"timezone\":\"UTC\",\"created_at\":\"2026-10-01T00:00:00.000Z\"}";

    @Test
    void listSlotsScopesByWorkspace() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":[" + SLOT + "]}");

        var slots = TestSupport.client(transport).queue().listSlots("w1");

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/queue/slots?workspace_id=w1", transport.last().url());
        assertEquals("s1", slots.get(0).id());
        assertEquals(1, slots.get(0).weekday());
        assertNull(slots.get(0).accountId());
    }

    @Test
    void createSlotSendsTheWorkspaceWithTheSlot() {
        FakeTransport transport = new FakeTransport().enqueue(201, SLOT);

        var slot = TestSupport.client(transport)
                .queue()
                .createSlot("w1", QueueSlotParams.create().weekday(1).time("09:30").timezone("UTC"));

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/queue/slots", transport.last().url());
        assertEquals(
                "{\"workspace_id\":\"w1\",\"weekday\":1,\"time\":\"09:30\",\"timezone\":\"UTC\"}",
                transport.lastBody());
        assertEquals("09:30", slot.time());
    }

    @Test
    void updateAndDeleteReportTheMovedPosts() {
        String moved = "\"moved\":[{\"id\":\"p1\",\"from\":\"2026-10-05T09:30:00Z\",\"to\":\"2026-10-05T10:00:00Z\"}]";
        FakeTransport transport = new FakeTransport()
                .enqueue(200, SLOT.substring(0, SLOT.length() - 1) + "," + moved + "}")
                .enqueue(200, "{\"deleted\":true," + moved + "}");
        FoPost client = TestSupport.client(transport);

        var updated = client.queue().updateSlot("s1", QueueSlotParams.create().time("10:00"));
        assertEquals("PUT", transport.last().method());
        assertEquals("https://api.fopost.test/v1/queue/slots/s1", transport.last().url());
        assertEquals("{\"time\":\"10:00\"}", transport.lastBody());
        assertEquals("p1", updated.moved().get(0).id());
        assertEquals(Instant.parse("2026-10-05T10:00:00Z"), updated.moved().get(0).to());

        var deleted = client.queue().deleteSlot("s1");
        assertEquals("DELETE", transport.last().method());
        assertEquals("https://api.fopost.test/v1/queue/slots/s1", transport.last().url());
        assertTrue(deleted.deleted());
        assertEquals(1, deleted.moved().size());
    }

    @Test
    void nextAndPreviewReadTimes() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"next\":\"2026-10-05T09:30:00.000Z\"}")
                .enqueue(200, "{\"next\":null}")
                .enqueue(200, "{\"data\":[\"2026-10-05T09:30:00Z\",\"2026-10-06T09:30:00Z\"]}");
        FoPost client = TestSupport.client(transport);

        assertEquals(Instant.parse("2026-10-05T09:30:00Z"), client.queue().next("w1", "a1"));
        assertEquals("https://api.fopost.test/v1/queue/next?workspace_id=w1&account_id=a1", transport.last().url());

        assertNull(client.queue().next("w1"));

        List<Instant> preview = client.queue().preview("w1", null, 2);
        assertEquals("https://api.fopost.test/v1/queue/preview?workspace_id=w1&count=2", transport.last().url());
        assertEquals(2, preview.size());
        assertEquals(Instant.parse("2026-10-06T09:30:00Z"), preview.get(1));
    }

    @Test
    void postsCanJoinTheQueue() {
        assertEquals("queue", CreatePostParams.of("w1").content("Hi").queue().toMap().get("schedule"));
        assertEquals("queue", UpdatePostParams.create().queue().toMap().get("schedule"));
    }
}
