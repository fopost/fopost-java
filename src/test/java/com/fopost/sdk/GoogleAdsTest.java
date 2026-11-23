package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.GoogleBidStrategy;
import com.fopost.sdk.model.GoogleQueryResult;
import com.fopost.sdk.param.GoogleAdsScope;
import com.fopost.sdk.param.GoogleBidStrategyParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoogleAdsTest {

    private static final GoogleAdsScope SCOPE = GoogleAdsScope.of("c1", "1234567890");

    @Test
    void bidStrategiesNameTheConnectionAndTheCustomer() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"1234567890~biddingStrategy~7","name":"Portfolio CPA",
                                  "type":"TARGET_CPA","status":"ENABLED","campaignCount":2}]}""");

        List<GoogleBidStrategy> strategies = TestSupport.client(transport).googleAds().bidStrategies(SCOPE);

        assertEquals("Portfolio CPA", strategies.get(0).name());
        assertEquals(2, strategies.get(0).campaignCount());
        String url = transport.last().url();
        assertTrue(url.contains("connection_id=c1"), url);
        assertTrue(url.contains("customer_id=1234567890"), url);
    }

    @Test
    void createBidStrategySendsTheScopeInACamelCaseBody() {
        FakeTransport transport = new FakeTransport().enqueue(201, """
                {"data":{"id":"1234567890~biddingStrategy~7"}}""");

        String id = TestSupport.client(transport)
                .googleAds()
                .createBidStrategy(new GoogleBidStrategyParams(
                                GoogleAdsScope.of("c1", "1234567890").workspace("w1"), "Portfolio CPA", "TARGET_CPA")
                        .targetMinor(500));

        assertEquals("1234567890~biddingStrategy~7", id);
        assertEquals("https://api.fopost.test/v1/ads/google/bid-strategies", transport.last().url());
        assertEquals(
                "{\"workspaceId\":\"w1\",\"connectionId\":\"c1\",\"customerId\":\"1234567890\","
                        + "\"name\":\"Portfolio CPA\",\"type\":\"TARGET_CPA\",\"targetMinor\":500}",
                transport.lastBody());
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
