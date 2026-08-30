package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.MediaItem;
import com.fopost.sdk.model.Page;
import com.fopost.sdk.model.Post;
import com.fopost.sdk.param.ContentBlockInput;
import com.fopost.sdk.param.CreatePostParams;
import com.fopost.sdk.param.PostListParams;
import com.fopost.sdk.param.PublishParams;
import com.fopost.sdk.param.UpdatePostParams;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PostsTest {

    @Test
    void listParsesItemsAndMeta() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"p1","status":"draft","workspace_id":"w1"}],
                         "meta":{"current_page":1,"per_page":30,"total":1,"last_page":1}}""");

        Page<Post> page = TestSupport.client(transport).posts().list();

        assertEquals(1, page.size());
        assertEquals("p1", page.data().get(0).id());
        assertEquals("w1", page.data().get(0).workspaceId());
        assertEquals(1, page.meta().total());
        assertEquals("p1", page.iterator().next().id());
    }

    @Test
    void createSendsTheBlocksAndUnwrapsABareResource() {
        FakeTransport transport = new FakeTransport().enqueue(201, "{\"id\":\"p1\",\"status\":\"scheduled\"}");

        Post post = TestSupport.client(transport)
                .posts()
                .create(CreatePostParams.of("w1")
                        .accounts("a1", "a2")
                        .content("First in the thread")
                        .block(ContentBlockInput.text("Second, with an image")
                                .media(MediaItem.of("image", "chart.png", "https://cdn.test/chart.png")))
                        .schedule(Instant.parse("2026-09-01T10:00:00Z")));

        assertEquals("p1", post.id());
        String body = transport.lastBody();
        assertTrue(body.contains("\"workspace_id\":\"w1\""));
        assertTrue(body.contains("\"accounts\":[\"a1\",\"a2\"]"));
        assertTrue(body.contains("\"status\":\"scheduled\""));
        assertTrue(body.contains("\"schedule_at\":\"2026-09-01T10:00:00Z\""));
        assertTrue(body.contains("\"text\":\"First in the thread\""));
        assertTrue(body.contains("\"url\":\"https://cdn.test/chart.png\""));
    }

    @Test
    void updateSendsOnlyTheFieldsThatWereSet() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"id\":\"p1\",\"status\":\"draft\"}");

        TestSupport.client(transport).posts().update("p1", UpdatePostParams.create().title("New title"));

        assertEquals("PUT", transport.last().method());
        assertEquals("{\"title\":\"New title\"}", transport.lastBody());
    }

    @Test
    void publishPostsToTheRightPathAndReadsTheEnvelope() {
        FakeTransport transport = new FakeTransport()
                .enqueue(202, """
                        {"data":{"post_status":"publishing",
                                 "deliveries":[{"id":"d1","accountId":"a1","status":"queued","attempts":0}],
                                 "healthWarnings":[]}}""");

        var result = TestSupport.client(transport).posts().publish("p1");

        assertEquals("https://api.fopost.test/v1/posts/p1/publish", transport.last().url());
        assertEquals("publishing", result.postStatus());
        assertEquals("a1", result.deliveries().get(0).accountId());
        assertFalse(result.isDryRun());
    }

    @Test
    void dryRunPublishReportsWithoutQueueing() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"dryRun\":true,\"post\":{\"id\":\"p1\",\"status\":\"draft\"}}}");

        var result = TestSupport.client(transport).posts().publish("p1", PublishParams.create().dryRun(true));

        assertTrue(transport.lastBody().contains("\"options\":{\"dryRun\":true}"));
        assertTrue(result.isDryRun());
        assertNull(result.postStatus());
    }

    @Test
    void bulkShiftSendsTheDiscriminatedAction() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"updated\":2,\"action\":\"shift\"}");

        var result = TestSupport.client(transport).posts().bulkShift("w1", List.of("p1", "p2"), -60);

        assertEquals(2, result.updated());
        String body = transport.lastBody();
        assertTrue(body.contains("\"action\":\"shift\""));
        assertTrue(body.contains("\"post_ids\":[\"p1\",\"p2\"]"));
        assertTrue(body.contains("\"offset_minutes\":-60"));
    }

    @Test
    void autoPaginateWalksEveryPageAndStopsAtTheLast() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"p1","status":"draft"},{"id":"p2","status":"draft"}],
                         "meta":{"current_page":1,"per_page":2,"total":3,"last_page":2}}""")
                .enqueue(200, """
                        {"data":[{"id":"p3","status":"draft"}],
                         "meta":{"current_page":2,"per_page":2,"total":3,"last_page":2}}""");

        List<String> ids = new ArrayList<>();
        for (Post post : TestSupport.client(transport)
                .posts()
                .autoPaginate(PostListParams.create().perPage(2))) {
            ids.add(post.id());
        }

        assertEquals(List.of("p1", "p2", "p3"), ids);
        assertEquals(2, transport.callCount());
        assertTrue(transport.requests.get(1).url().contains("page=2"));
    }

    @Test
    void deliveriesUnwrapsTheEnvelope() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"d1","accountId":"a1","status":"published","platform":"twitter",
                                  "externalUrl":"https://x.com/1","attempts":1}]}""");

        var deliveries = TestSupport.client(transport).posts().deliveries("p1");

        assertEquals(1, deliveries.size());
        assertEquals("https://x.com/1", deliveries.get(0).externalUrl());
        assertEquals("twitter", deliveries.get(0).platform());
    }
}
