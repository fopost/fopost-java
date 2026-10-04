package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.GoogleKeyword;
import com.fopost.sdk.model.GoogleOptimizationScore;
import com.fopost.sdk.model.GoogleQueryResult;
import com.fopost.sdk.model.GoogleRecommendation;
import com.fopost.sdk.param.GoogleAdsScope;
import com.fopost.sdk.param.GoogleKeywordParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoogleAdsTest {

    private static final GoogleAdsScope SCOPE = GoogleAdsScope.of("c1", "1234567890");

    @Test
    void keywordsNameTheConnectionAndTheCustomer() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"1234567890~keyword~77~99","adGroupId":"1234567890~adGroup~77",
                                  "text":"running shoes","matchType":"EXACT","status":"ENABLED",
                                  "cpcBidMinor":180,"negative":false}]}""");

        List<GoogleKeyword> keywords = TestSupport.client(transport)
                .googleAds()
                .keywords(SCOPE, "1234567890~adGroup~77");

        assertEquals("running shoes", keywords.get(0).text());
        assertEquals(180L, keywords.get(0).cpcBidMinor());
        String url = transport.last().url();
        assertTrue(url.contains("connection_id=c1"), url);
        assertTrue(url.contains("customer_id=1234567890"), url);
        assertTrue(url.contains("ad_group_id=1234567890%7EadGroup%7E77"), url);
    }

    @Test
    void createKeywordSendsTheScopeInACamelCaseBody() {
        FakeTransport transport = new FakeTransport().enqueue(201, """
                {"data":{"id":"1234567890~keyword~77~99"}}""");

        String id = TestSupport.client(transport)
                .googleAds()
                .createKeyword(new GoogleKeywordParams.Create(
                                GoogleAdsScope.of("c1", "1234567890").workspace("w1"),
                                "1234567890~adGroup~77",
                                "running shoes",
                                "EXACT")
                        .cpcBidMinor(180));

        assertEquals("1234567890~keyword~77~99", id);
        assertEquals("https://api.fopost.test/v1/ads/google/keywords", transport.last().url());
        assertEquals(
                "{\"workspaceId\":\"w1\",\"connectionId\":\"c1\",\"customerId\":\"1234567890\","
                        + "\"adGroupId\":\"1234567890~adGroup~77\",\"text\":\"running shoes\","
                        + "\"matchType\":\"EXACT\",\"cpcBidMinor\":180}",
                transport.lastBody());
    }

    @Test
    void recommendationsJoinTheTypesFilter() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"customers/1234567890/recommendations/ABC~1","type":"KEYWORD",
                                  "campaignId":"1234567890~campaign~55","adGroupId":null,
                                  "dismissed":false,
                                  "impact":{"baseClicks":10,"potentialClicks":25,
                                            "baseCostMinor":100,"potentialCostMinor":250,
                                            "baseConversions":1,"potentialConversions":3}}]}""");

        List<GoogleRecommendation> rows = TestSupport.client(transport)
                .googleAds()
                .recommendations(SCOPE, List.of("KEYWORD", "TARGET_CPA_OPT_IN"));

        assertEquals("KEYWORD", rows.get(0).type());
        assertEquals(25.0, rows.get(0).impact().potentialClicks());
        assertFalse(rows.get(0).dismissed());
        assertTrue(transport.last().url().contains("types=KEYWORD%2CTARGET_CPA_OPT_IN"));
    }

    @Test
    void recommendationsOmitTheTypesFilterWhenNoneAreGiven() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":[]}");

        TestSupport.client(transport).googleAds().recommendations(SCOPE);

        assertFalse(transport.last().url().contains("types="), transport.last().url());
    }

    @Test
    void applyRecommendationsSendsTheIdsAndCountsWhatLanded() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":{\"applied\":1}}");

        int applied = TestSupport.client(transport)
                .googleAds()
                .applyRecommendations(
                        GoogleAdsScope.of("c1", "1234567890").workspace("w1"),
                        List.of("customers/1234567890/recommendations/ABC~1"));

        assertEquals(1, applied);
        assertEquals(
                "https://api.fopost.test/v1/ads/google/recommendations/apply",
                transport.last().url());
        assertTrue(transport.lastBody().contains("customers/1234567890/recommendations/ABC~1"));
    }

    @Test
    void optimizationScoreReadsTheAccountAndItsCampaigns() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"score":0.82,"weight":1.0,
                                 "campaigns":[{"id":"1234567890~campaign~55","name":"Search","score":0.75}]}}""");

        GoogleOptimizationScore score =
                TestSupport.client(transport).googleAds().optimizationScore(SCOPE);

        assertEquals(0.82, score.score());
        assertEquals("Search", score.campaigns().get(0).name());
    }

    @Test
    void queryReturnsRowsAsGoogleSendsThem() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"rows":[{"campaign":{"id":"55"}}]}}""");

        GoogleQueryResult result = TestSupport.client(transport)
                .googleAds()
                .query(SCOPE, "SELECT campaign.id FROM campaign");

        assertEquals(1, result.rows().size());
        assertEquals("https://api.fopost.test/v1/ads/insights/query", transport.last().url());
    }

    @Test
    void authorizeGoogleGoesThroughTheGenericProviderRoute() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"url\":\"https://accounts.google.com/o/x\"}}");

        String url = TestSupport.client(transport).ads().authorize("google", "w1");

        assertEquals("https://accounts.google.com/o/x", url);
        assertEquals(
                "https://api.fopost.test/v1/ads/connections/google/authorize",
                transport.last().url());
    }
}
