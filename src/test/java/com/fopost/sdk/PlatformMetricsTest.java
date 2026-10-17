package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.AccountPlatformMetrics;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlatformMetricsTest {

    @Test
    void platformMetricsAsksForRawAndDecodesTheSet() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"platform":"facebook",
                          "account":{"fetched_at":"2026-09-20T02:00:00.000Z","metrics":[
                            {"key":"page_daily_video_ad_break_earnings","label":"Ad Break Earnings",
                             "kind":"currency_usd","value":42.15},
                            {"key":"page_impressions_paid","label":"Paid Impressions",
                             "kind":"count","value":1500}]},
                          "post":{"external_post_id":"123_456","fetched_at":"2026-09-20T02:00:00.000Z",
                                  "metrics":[]}}}""");

        AccountPlatformMetrics metrics = TestSupport.client(transport).accounts().platformMetrics("a1");

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/insights?raw=true", transport.last().url());
        assertEquals("facebook", metrics.platform());
        assertEquals("2026-09-20T02:00:00.000Z", metrics.account().fetchedAt());
        assertEquals(
                List.of("page_daily_video_ad_break_earnings", "page_impressions_paid"),
                metrics.account().metrics().stream().map(AccountPlatformMetrics.Row::key).toList());
        assertEquals(42.15, metrics.account().metrics().get(0).number());
        assertEquals("123_456", metrics.post().externalPostId());
        assertTrue(metrics.post().metrics().isEmpty());
    }

    @Test
    void aSeriesValueSurvivesAsAList() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"platform":"youtube",
                          "account":{"fetched_at":null,"metrics":[
                            {"key":"daily_views","label":"Views by Day","kind":"series",
                             "value":[{"day":"2026-09-19","views":600}]}]},
                          "post":{"external_post_id":null,"fetched_at":null,"metrics":[]}}}""");

        AccountPlatformMetrics metrics = TestSupport.client(transport).accounts().platformMetrics("a1");
        AccountPlatformMetrics.Row row = metrics.account().metrics().get(0);

        assertNull(row.number());
        assertTrue(row.value() instanceof List<?>);
        assertNull(metrics.account().fetchedAt());
    }

    @Test
    void aPendingMetricGrantThrows() {
        FakeTransport transport = new FakeTransport()
                .enqueue(503, """
                        {"error":"platform_metrics_unavailable",
                         "message":"google-business metrics are not available on this deployment yet."}""");

        FoPostException error = assertThrows(FoPostException.class,
                () -> TestSupport.client(transport, 1, new java.util.ArrayList<>())
                        .accounts()
                        .platformMetrics("a1"));

        assertEquals(503, error.status());
        assertEquals("platform_metrics_unavailable", error.code());
    }
}
