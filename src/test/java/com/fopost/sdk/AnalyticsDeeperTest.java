package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.param.AnalyticsParams;
import org.junit.jupiter.api.Test;

/** The deeper analytics endpoints: decay, cadence, per-post timelines, changes and native posts. */
class AnalyticsDeeperTest {

    @Test
    void decayReadsTheBandsAndTheHalfLife() {
        FakeTransport transport = new FakeTransport()
                .enqueue(
                        200,
                        "{\"data\":{\"days\":30,\"postsMeasured\":2,\"halfLifeBucket\":\"1h_3h\",\"bands\":["
                                + "{\"bucket\":\"under_1h\",\"label\":\"First hour\",\"posts\":2,"
                                + "\"avgEngagements\":25,\"avgImpressions\":300,\"shareOfFinal\":0.3},"
                                + "{\"bucket\":\"6h_12h\",\"label\":\"6-12 hours\",\"posts\":0,"
                                + "\"avgEngagements\":0,\"avgImpressions\":0,\"shareOfFinal\":null}]}}");

        var decay = TestSupport.client(transport)
                .analytics()
                .decay(AnalyticsParams.create().days(30).accountId("a1"));

        assertEquals("https://api.fopost.test/v1/analytics/decay?accountId=a1&days=30", transport.last().url());
        assertEquals("1h_3h", decay.halfLifeBucket());
        assertEquals(2, decay.postsMeasured());
        assertEquals(0.3, decay.bands().get(0).shareOfFinal());
        // A band nothing was measured in reports no share rather than zero
        assertNull(decay.bands().get(1).shareOfFinal());
    }

    @Test
    void frequencyReadsTheWeeksAndTheBestCadence() {
        FakeTransport transport = new FakeTransport()
                .enqueue(
                        200,
                        "{\"data\":{\"days\":90,\"weeks\":[{\"weekStart\":\"2026-03-02\",\"posts\":2,"
                                + "\"engagements\":240,\"avgEngagementsPerPost\":120}],"
                                + "\"bands\":[{\"band\":\"under_3\",\"label\":\"1-2 a week\",\"weeks\":1,"
                                + "\"posts\":2,\"avgPostsPerWeek\":2,\"avgEngagementsPerPost\":120,"
                                + "\"engagementRate\":0.12}],"
                                + "\"best\":{\"band\":\"under_3\",\"label\":\"1-2 a week\","
                                + "\"avgEngagementsPerPost\":120}}}");

        var frequency =
                TestSupport.client(transport).analytics().frequency(AnalyticsParams.create().days(90));

        assertEquals("2026-03-02", frequency.weeks().get(0).weekStart());
        assertEquals(0.12, frequency.bands().get(0).engagementRate());
        assertEquals("1-2 a week", frequency.best().label());
    }

    @Test
    void aTimelineCanBeAddressedByPermalink() {
        FakeTransport transport = new FakeTransport()
                .enqueue(
                        200,
                        "{\"data\":{\"postId\":null,\"deliveries\":[{\"accountId\":\"a1\","
                                + "\"platform\":\"twitter\",\"username\":\"acme\",\"externalPostId\":\"1\","
                                + "\"postedAt\":\"2026-03-02T00:00:00.000Z\",\"points\":["
                                + "{\"at\":\"2026-03-02T00:30:00.000Z\",\"ageMinutes\":30,\"engagements\":40,"
                                + "\"impressions\":400,\"reach\":null,\"likes\":30,\"comments\":null,"
                                + "\"shares\":null,\"videoViews\":null,\"delta\":{\"impressions\":400,"
                                + "\"reach\":0,\"engagements\":40,\"likes\":30,\"comments\":0,"
                                + "\"shares\":0}}]}]}}");

        var timeline = TestSupport.client(transport).analytics().timeline("https://x.com/acme/status/1");

        assertEquals(
                "https://api.fopost.test/v1/analytics/posts/https%3A%2F%2Fx.com%2Facme%2Fstatus%2F1/timeline",
                transport.last().url());
        // A post made on the network has no FoPost id
        assertNull(timeline.postId());
        var point = timeline.deliveries().get(0).points().get(0);
        assertEquals(30, point.ageMinutes());
        assertEquals(40, point.delta().engagements());
    }

    @Test
    void changesCarriesTheCursor() {
        FakeTransport transport = new FakeTransport()
                .enqueue(
                        200,
                        "{\"data\":{\"since\":\"2026-03-02T00:00:00.000Z\","
                                + "\"cursor\":\"2026-03-02T06:00:00.000Z\",\"hasMore\":true,\"changes\":["
                                + "{\"accountId\":\"a1\",\"platform\":\"twitter\",\"externalPostId\":\"1\","
                                + "\"postId\":\"p1\",\"postedAt\":\"2026-03-02T00:00:00.000Z\","
                                + "\"fetchedAt\":\"2026-03-02T06:00:00.000Z\",\"impressions\":900,"
                                + "\"reach\":null,\"engagements\":90,\"likes\":70,\"comments\":10,"
                                + "\"shares\":10}]}}");

        var page = TestSupport.client(transport)
                .analytics()
                .changes(AnalyticsParams.create().since("2026-03-02T00:00:00Z").limit(100));

        assertTrue(transport.last().url().contains("since=2026-03-02T00%3A00%3A00Z"));
        assertTrue(page.hasMore());
        assertEquals("p1", page.changes().get(0).postId());
    }

    @Test
    void collectPostReportsEachDelivery() {
        FakeTransport transport = new FakeTransport()
                .enqueue(
                        200,
                        "{\"data\":{\"collected\":1,\"deliveries\":[{\"accountId\":\"a1\","
                                + "\"platform\":\"twitter\",\"externalPostId\":\"1\",\"collected\":true,"
                                + "\"fetchedAt\":\"2026-03-02T00:30:00.000Z\",\"message\":null}]}}");

        var result = TestSupport.client(transport).analytics().collectPost("p1");

        assertEquals("https://api.fopost.test/v1/posts/p1/analytics/collect", transport.last().url());
        assertEquals(1, result.collected());
        assertTrue(result.deliveries().get(0).collected());
    }

    @Test
    void nativePostsKeepsTheMetaEnvelope() {
        FakeTransport transport = new FakeTransport()
                .enqueue(
                        200,
                        "{\"data\":[{\"externalPostId\":\"1\",\"text\":\"Posted by hand\","
                                + "\"permalink\":\"https://x.com/acme/status/1\",\"thumbnailUrl\":null,"
                                + "\"mediaType\":null,\"postedAt\":\"2026-03-02T00:00:00.000Z\","
                                + "\"fetchedAt\":\"2026-03-02T06:00:00.000Z\",\"metrics\":{\"impressions\":900,"
                                + "\"reach\":null,\"engagements\":90,\"likes\":70,\"comments\":10,"
                                + "\"shares\":10,\"videoViews\":null}}],"
                                + "\"meta\":{\"page\":1,\"perPage\":20,\"total\":1}}");

        var page = TestSupport.client(transport)
                .analytics()
                .nativePosts("a1", AnalyticsParams.create().page(1).perPage(20));

        assertEquals("https://api.fopost.test/v1/accounts/a1/native-posts?page=1&per_page=20", transport.last().url());
        assertEquals(1, page.size());
        assertEquals("https://x.com/acme/status/1", page.data().get(0).permalink());
        assertEquals(90, page.data().get(0).metrics().engagements());
        assertEquals(20, page.meta().perPage());
    }
}
