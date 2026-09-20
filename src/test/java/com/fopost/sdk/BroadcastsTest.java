package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.AudienceFilter;
import com.fopost.sdk.model.Broadcast;
import com.fopost.sdk.model.BroadcastPage;
import com.fopost.sdk.model.BroadcastSent;
import com.fopost.sdk.model.Enrolled;
import com.fopost.sdk.model.RecipientPage;
import com.fopost.sdk.model.Sequence;
import com.fopost.sdk.model.SequenceStep;
import com.fopost.sdk.model.Unenrolled;
import com.fopost.sdk.param.BroadcastParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class BroadcastsTest {

    private static final String BROADCAST =
            """
            {"id":"bc_1","name":"September check-in","text":"New colours just landed.",
             "account_id":"acc_1","audience":{"platforms":["instagram"]},
             "status":"sent","scheduled_at":null,
             "sent_at":"2026-09-19T10:04:00.000Z","created_at":"2026-09-19T09:58:00.000Z",
             "counts":{"total":3,"sent":2,"skipped":1,"failed":0,"pending":0}}""";

    @Test
    void listReadsThePaginationBlockRatherThanMeta() {
        FakeTransport transport =
                new FakeTransport()
                        .enqueue(
                                200,
                                "{\"data\":[" + BROADCAST + "],\"pagination\":{\"page\":2,\"per_page\":10,\"total\":11}}");

        BroadcastPage page =
                TestSupport.client(transport)
                        .broadcasts()
                        .list(BroadcastParams.Filter.create().workspace("w1").status("sent").page(2).perPage(10));

        assertEquals(
                "https://api.fopost.test/v1/broadcasts?workspace_id=w1&status=sent&page=2&per_page=10",
                transport.last().url());
        assertEquals(1, page.size());
        Broadcast broadcast = page.data().get(0);
        assertEquals("September check-in", broadcast.name());
        assertEquals(2, broadcast.counts().sent());
        assertEquals(1, broadcast.counts().skipped());
        assertEquals(11, page.pagination().total());
    }

    @Test
    void createSendsTheSnakeCaseBody() {
        FakeTransport transport = new FakeTransport().enqueue(201, "{\"data\":" + BROADCAST + "}");

        TestSupport.client(transport)
                .broadcasts()
                .create(
                        BroadcastParams.Create.of("w1", "acc_1", "September check-in", "New colours just landed.")
                                .audience(AudienceFilter.all().platforms("instagram"))
                                .scheduledAt("2026-10-01T09:00:00.000Z"));

        String body = transport.lastBody();
        assertTrue(body.contains("\"workspace_id\":\"w1\""), body);
        assertTrue(body.contains("\"account_id\":\"acc_1\""), body);
        assertTrue(body.contains("\"scheduled_at\":\"2026-10-01T09:00:00.000Z\""), body);
        assertTrue(body.contains("\"platforms\":[\"instagram\"]"), body);
    }

    /** A closed messaging window has to be readable, or a non-send is a mystery. */
    @Test
    void aSkippedRecipientKeepsItsReason() {
        FakeTransport transport =
                new FakeTransport()
                        .enqueue(
                                200,
                                """
                                {"data":[{"contact_id":"con_1","display_name":"Sam Rivera",
                                          "status":"skipped","skip_reason":"window_closed",
                                          "sent_at":null,"error":null}],
                                 "pagination":{"page":1,"per_page":50,"total":1}}""");

        RecipientPage page =
                TestSupport.client(transport)
                        .broadcasts()
                        .recipients("bc_1", BroadcastParams.Recipients.create().status("skipped"));

        assertTrue(transport.last().url().endsWith("/v1/broadcasts/bc_1/recipients?status=skipped"),
                transport.last().url());
        assertEquals("skipped", page.data().get(0).status());
        assertEquals("window_closed", page.data().get(0).skipReason());
    }

    @Test
    void sendReportsHowManyMatched() {
        FakeTransport transport =
                new FakeTransport()
                        .enqueue(200, "{\"data\":{\"id\":\"bc_1\",\"status\":\"sending\",\"recipients\":3}}");

        BroadcastSent sent = TestSupport.client(transport).broadcasts().send("bc_1");

        assertTrue(transport.last().url().endsWith("/v1/broadcasts/bc_1/send"), transport.last().url());
        assertEquals(3, sent.recipients());
        assertEquals("sending", sent.status());
    }

    @Test
    void sequenceStepsTravelAsGiven() {
        FakeTransport transport =
                new FakeTransport()
                        .enqueue(
                                201,
                                """
                                {"data":{"id":"seq_1","name":"Welcome","account_id":"acc_1",
                                 "steps":[{"delay_hours":0,"text":"Hi"},{"delay_hours":48,"text":"Still here?"}],
                                 "status":"active","created_at":"2026-09-12T08:00:00.000Z"}}""");

        Sequence sequence =
                TestSupport.client(transport)
                        .sequences()
                        .create(
                                BroadcastParams.CreateSequence.of(
                                        "w1", "acc_1", "Welcome", List.of(SequenceStep.of(0, "Hi"))));

        assertEquals(48.0, sequence.steps().get(1).delayHours());
        String body = transport.lastBody();
        assertTrue(body.contains("\"delay_hours\":0.0") || body.contains("\"delay_hours\":0"), body);
        assertTrue(body.contains("\"text\":\"Hi\""), body);
    }

    @Test
    void enrollTakesIdsOrAnAudience() {
        FakeTransport transport =
                new FakeTransport()
                        .enqueue(200, "{\"data\":{\"id\":\"seq_1\",\"enrolled\":2}}")
                        .enqueue(200, "{\"data\":{\"id\":\"seq_1\",\"enrolled\":5}}");
        FoPost client = TestSupport.client(transport);

        Enrolled byId = client.sequences().enroll("seq_1", BroadcastParams.Enroll.contacts(List.of("con_1", "con_2")));
        assertEquals(2, byId.enrolled());
        assertTrue(transport.lastBody().contains("\"contact_ids\":[\"con_1\",\"con_2\"]"), transport.lastBody());

        client.sequences()
                .enroll("seq_1", BroadcastParams.Enroll.audience(AudienceFilter.all().platforms("telegram")));
        assertTrue(transport.lastBody().contains("\"platforms\":[\"telegram\"]"), transport.lastBody());
    }

    @Test
    void unenrollNamesTheContactsItStops() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":{\"id\":\"seq_1\",\"stopped\":1}}");

        Unenrolled stopped = TestSupport.client(transport).sequences().unenroll("seq_1", List.of("con_1"));

        assertTrue(transport.last().url().endsWith("/v1/sequences/seq_1/unenroll"), transport.last().url());
        assertTrue(transport.lastBody().contains("\"contact_ids\":[\"con_1\"]"), transport.lastBody());
        assertEquals(1, stopped.stopped());
    }
}
