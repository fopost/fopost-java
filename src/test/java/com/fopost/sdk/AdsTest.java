package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.Ad;
import com.fopost.sdk.model.AdAccountTree;
import com.fopost.sdk.model.AdCampaign;
import com.fopost.sdk.model.AdCreative;
import com.fopost.sdk.model.AdInsightsReport;
import com.fopost.sdk.model.AudiencesResult;
import com.fopost.sdk.model.BulkAdStatusResult;
import com.fopost.sdk.model.LeadsFeed;
import com.fopost.sdk.model.LeadsPage;
import com.fopost.sdk.param.AdBudgetParams;
import com.fopost.sdk.param.AdInsightsParams;
import com.fopost.sdk.param.AdTargetingParams;
import com.fopost.sdk.param.BoostPostParams;
import com.fopost.sdk.param.BulkAdStatusParams;
import com.fopost.sdk.param.CreateAdCampaignParams;
import com.fopost.sdk.param.CreateAdCreativeParams;
import com.fopost.sdk.param.CreateAdParams;
import com.fopost.sdk.param.CreateAudienceParams;
import com.fopost.sdk.param.CreateLeadFormParams;
import com.fopost.sdk.param.LeadsFeedParams;
import com.fopost.sdk.param.UpdateAdCampaignParams;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdsTest {

    @Test
    void boostSendsACamelCaseBodyAndParsesTheAd() {
        FakeTransport transport = new FakeTransport()
                .enqueue(201, """
                        {"data":{"id":"ad1","workspaceId":"w1","kind":"boost","name":"Launch week",
                                 "goal":"engagement","status":"paused","effectiveStatus":null,
                                 "connectionId":"c1","accountId":"a1","platform":"facebook",
                                 "adAccountId":"act_123","sourcePostId":"p1","budgetMinor":2000,
                                 "budgetType":"daily","currency":"USD","endAt":null,
                                 "targeting":{"countries":["US"],"ageMin":21,"ageMax":45,"gender":"all",
                                              "interests":[{"id":"6003","name":"Coffee"}]},
                                 "creative":null,"insights":null,"insightsAt":null,"lastError":null,
                                 "createdAt":"2026-09-01T10:00:00.000Z"}}""");

        Ad ad = TestSupport.client(transport)
                .ads()
                .boost(BoostPostParams.of(
                                "w1", "c1", "act_123", "p1", "a1", "Launch week", "engagement",
                                AdBudgetParams.daily(2000),
                                AdTargetingParams.create(List.of("US"), 21, 45, "all").interest("6003", "Coffee"))
                        .paused(false));

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/ads/boost", transport.last().url());
        assertEquals(
                "{\"workspaceId\":\"w1\",\"connectionId\":\"c1\",\"adAccountId\":\"act_123\",\"postId\":\"p1\","
                        + "\"accountId\":\"a1\",\"name\":\"Launch week\",\"goal\":\"engagement\","
                        + "\"budget\":{\"minor\":2000,\"type\":\"daily\"},"
                        + "\"targeting\":{\"countries\":[\"US\"],\"ageMin\":21,\"ageMax\":45,\"gender\":\"all\","
                        + "\"interests\":[{\"id\":\"6003\",\"name\":\"Coffee\"}]},\"paused\":false}",
                transport.lastBody());
        assertEquals("boost", ad.kind());
        assertEquals(2000L, ad.budgetMinor());
        assertEquals("Coffee", ad.targeting().interests().get(0).name());
        assertNull(ad.insights());
        assertEquals(Instant.parse("2026-09-01T10:00:00Z"), ad.createdAt());
    }

    @Test
    void statusRefreshAndDeleteCarryTheWorkspaceInTheQuery() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"id\":\"ad1\",\"status\":\"active\"}}")
                .enqueue(200, "{\"data\":{\"id\":\"ad1\",\"insights\":{\"impressions\":120,\"reach\":90,"
                        + "\"clicks\":7,\"spendMinor\":350}}}")
                .enqueue(200, "{\"message\":\"deleted\"}");

        FoPost client = TestSupport.client(transport);

        assertEquals("active", client.ads().setStatus("ad1", "w1", "active").status());
        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/ads/ad1?workspace_id=w1", transport.last().url());
        assertEquals("{\"status\":\"active\"}", transport.lastBody());

        assertEquals(350L, client.ads().refresh("ad1", "w1").insights().spendMinor());
        assertEquals("https://api.fopost.test/v1/ads/ad1/refresh?workspace_id=w1", transport.last().url());

        client.ads().delete("ad1", "w1");
        assertEquals("DELETE", transport.last().method());
        assertEquals("https://api.fopost.test/v1/ads/ad1?workspace_id=w1", transport.last().url());
    }

    @Test
    void audiencesTargetingAndLeadsReadTheirQueriesAndBodies() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"audiences\":[{\"id\":\"au1\",\"name\":\"Buyers\",\"subtype\":\"CUSTOM\"}],"
                        + "\"pixels\":[{\"id\":\"px1\",\"name\":\"Site\"}],\"workspaceId\":\"w1\"}}")
                .enqueue(201, "{\"data\":{\"id\":\"au2\",\"added\":2}}")
                .enqueue(200, "{\"data\":[{\"id\":\"6003\",\"name\":\"Coffee\",\"detail\":\"Interest\"}]}")
                .enqueue(201, "{\"data\":{\"id\":\"form1\"}}")
                .enqueue(200, "{\"data\":{\"leads\":[{\"id\":\"l1\",\"fields\":[{\"name\":\"full_name\","
                        + "\"values\":[\"Jordan Vale\"]}],\"isOrganic\":false}],\"nextCursor\":\"abc\"}}");

        FoPost client = TestSupport.client(transport);

        AudiencesResult audiences = client.ads().audiences("c1", "act_123");
        assertEquals("https://api.fopost.test/v1/ads/audiences?connection_id=c1&ad_account_id=act_123",
                transport.last().url());
        assertEquals("Buyers", audiences.audiences().get(0).name());
        assertEquals("px1", audiences.pixels().get(0).id());

        var created = client.ads().createAudience(CreateAudienceParams.lookalike(
                        "w1", "c1", "act_123", "Like buyers", "au1", "US")
                .ratio(0.05));
        assertEquals(2, created.added());
        assertEquals(
                "{\"workspaceId\":\"w1\",\"connectionId\":\"c1\",\"adAccountId\":\"act_123\",\"name\":\"Like buyers\","
                        + "\"spec\":{\"subtype\":\"LOOKALIKE\",\"originAudienceId\":\"au1\",\"country\":\"US\",\"ratio\":0.05}}",
                transport.lastBody());

        var options = client.ads().searchTargeting("c1", "interest", "coffee");
        assertEquals("https://api.fopost.test/v1/ads/targeting/search?connection_id=c1&type=interest&q=coffee",
                transport.last().url());
        assertEquals("Coffee", options.get(0).name());

        String formId = client.ads().createLeadForm(CreateLeadFormParams.of(
                "w1", "c1", "page1", "Newsletter", List.of("EMAIL", "FULL_NAME"),
                "https://yourbrand.com/privacy", "Thanks, talk soon."));
        assertEquals("form1", formId);
        assertTrue(transport.lastBody().contains("\"questions\":[\"EMAIL\",\"FULL_NAME\"]"));
        assertTrue(transport.lastBody().contains("\"privacyPolicyUrl\":\"https://yourbrand.com/privacy\""));

        LeadsPage leads = client.ads().leads("form1", "c1", "page1", "abc", null);
        assertEquals(
                "https://api.fopost.test/v1/ads/lead-forms/form1/leads?connection_id=c1&page_id=page1&after=abc",
                transport.last().url());
        assertEquals("abc", leads.nextCursor());
        assertEquals(List.of("Jordan Vale"), leads.leads().get(0).fields().get(0).values());
    }

    @Test
    void accountTreeNestsAdSetsAndAdsUnderCampaigns() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"adAccountId":"act_123","currency":"USD","workspaceId":"w1",
                                 "campaigns":[{"id":"cmp1","name":"Launch","status":"PAUSED","objective":"OUTCOME_TRAFFIC",
                                               "budgetMinor":null,"budgetType":null,
                                               "adSets":[{"id":"set1","name":"US","campaignId":"cmp1","status":"PAUSED",
                                                          "budgetMinor":2000,"budgetType":"daily",
                                                          "ads":[{"id":"ad9","name":"A","adSetId":"set1",
                                                                  "creativeId":"cr1","status":"PAUSED"}]}]}]}}""");

        AdAccountTree tree = TestSupport.client(transport).ads().accountTree("act_123", "c1", "w1");

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/ads/accounts/act_123/tree?workspace_id=w1&connection_id=c1",
                transport.last().url());
        assertNull(tree.campaigns().get(0).budgetMinor());
        assertEquals(2000L, tree.campaigns().get(0).adSets().get(0).budgetMinor());
        assertEquals("cr1", tree.campaigns().get(0).adSets().get(0).ads().get(0).creativeId());
    }

    @Test
    void campaignWritesCarryTheConnectionAndDuplicateReturnsTheCopyId() {
        FakeTransport transport = new FakeTransport()
                .enqueue(201, "{\"data\":{\"id\":\"cmp1\",\"name\":\"Launch\",\"status\":\"PAUSED\"}}")
                .enqueue(200, "{\"data\":{\"id\":\"cmp1\",\"name\":\"Launch\",\"status\":\"ACTIVE\"}}")
                .enqueue(201, "{\"data\":{\"id\":\"cmp2\"}}")
                .enqueue(200, "{\"data\":[{\"id\":\"cmp1\",\"level\":\"campaign\",\"ok\":true,\"error\":null},"
                        + "{\"id\":\"ad9\",\"level\":\"ad\",\"ok\":false,\"error\":\"Not found\"}]}");

        FoPost client = TestSupport.client(transport);

        AdCampaign created = client.ads().createCampaign(
                CreateAdCampaignParams.of("w1", "c1", "act_123", "Launch", "traffic"));
        assertEquals("https://api.fopost.test/v1/ads/campaigns", transport.last().url());
        assertEquals(
                "{\"workspaceId\":\"w1\",\"connectionId\":\"c1\",\"adAccountId\":\"act_123\",\"name\":\"Launch\","
                        + "\"goal\":\"traffic\"}",
                transport.lastBody());
        assertEquals("PAUSED", created.status());

        client.ads().updateCampaign("cmp1", "w1", "c1", UpdateAdCampaignParams.create().status("active"));
        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/ads/campaigns/cmp1?workspace_id=w1&connection_id=c1",
                transport.last().url());
        assertEquals("{\"status\":\"active\"}", transport.lastBody());

        assertEquals("cmp2", client.ads().duplicateCampaign("cmp1", "w1", "c1", false));
        assertEquals("https://api.fopost.test/v1/ads/campaigns/cmp1/duplicate?workspace_id=w1&connection_id=c1",
                transport.last().url());
        assertEquals("{\"paused\":false}", transport.lastBody());

        List<BulkAdStatusResult> results = client.ads().bulkSetStatus(
                BulkAdStatusParams.of("w1", "c1", "paused").campaign("cmp1").ad("ad9"));
        assertEquals(
                "{\"workspaceId\":\"w1\",\"connectionId\":\"c1\",\"status\":\"paused\","
                        + "\"objects\":[{\"id\":\"cmp1\",\"level\":\"campaign\"},{\"id\":\"ad9\",\"level\":\"ad\"}]}",
                transport.lastBody());
        assertEquals("Not found", results.get(1).error());
    }

    @Test
    void insightsSendTheRangeBreakdownAndDailyFlag() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"objectId":"cmp1","currency":"USD","since":"2026-09-01","until":"2026-09-07",
                                 "breakdownBy":"age",
                                 "totals":{"impressions":1000,"reach":800,"clicks":40,"spendMinor":1500,"ctr":4.0,"leads":3},
                                 "breakdown":[{"key":"25-34","metrics":{"impressions":600,"reach":500,"clicks":30,
                                               "spendMinor":900,"ctr":5.0,"leads":2}}],
                                 "timeline":[{"date":"2026-09-01","metrics":{"impressions":100,"reach":90,"clicks":4,
                                              "spendMinor":150,"ctr":4.0,"leads":0}}]}}""")
                .enqueue(200, "{\"data\":{\"objectId\":\"x\",\"since\":\"2026-09-01\",\"until\":\"2026-09-07\","
                        + "\"totals\":null,\"breakdown\":[],\"timeline\":[]}}");

        FoPost client = TestSupport.client(transport);

        AdInsightsReport report = client.ads().insights(
                "c1", "cmp1", AdInsightsParams.of("2026-09-01", "2026-09-07").breakdown("age").daily(true), "w1");
        assertEquals(
                "https://api.fopost.test/v1/ads/insights?workspace_id=w1&connection_id=c1&object_id=cmp1"
                        + "&since=2026-09-01&until=2026-09-07&breakdown=age&daily=true",
                transport.last().url());
        assertEquals(4.0, report.totals().ctr());
        assertEquals("25-34", report.breakdown().get(0).key());
        assertEquals(150L, report.timeline().get(0).metrics().spendMinor());

        AdInsightsReport empty = client.ads().adInsights("ad1", "w1", AdInsightsParams.of("2026-09-01", "2026-09-07"));
        assertEquals("https://api.fopost.test/v1/ads/ad1/insights?workspace_id=w1&since=2026-09-01&until=2026-09-07",
                transport.last().url());
        assertNull(empty.totals());
    }

    @Test
    void leadsFeedPassesTheCursorBack() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"leads\":[{\"id\":\"f1\",\"leadId\":\"m1\",\"pageId\":\"page1\","
                        + "\"isOrganic\":true,\"fields\":[{\"name\":\"email\",\"values\":[\"jordan@yourbrand.com\"]}],"
                        + "\"submittedAt\":\"2026-09-02T08:00:00.000Z\"}],\"nextCursor\":\"cur2\"}}")
                .enqueue(200, "{\"data\":{\"leads\":[],\"nextCursor\":null}}");

        FoPost client = TestSupport.client(transport);

        LeadsFeed first = client.ads().leadsFeed(LeadsFeedParams.create().workspaceId("w1").formId("form1").limit(50));
        assertEquals("https://api.fopost.test/v1/ads/leads?workspace_id=w1&form_id=form1&limit=50",
                transport.last().url());
        assertEquals("cur2", first.nextCursor());
        assertEquals("m1", first.leads().get(0).leadId());
        assertEquals(Instant.parse("2026-09-02T08:00:00Z"), first.leads().get(0).submittedAt());

        LeadsFeed next = client.ads().leadsFeed(LeadsFeedParams.create().workspaceId("w1").cursor(first.nextCursor()));
        assertEquals("https://api.fopost.test/v1/ads/leads?workspace_id=w1&cursor=cur2", transport.last().url());
        assertNull(next.nextCursor());
    }

    @Test
    void creativesAndAdUrlTagsTravelInTheBody() {
        FakeTransport transport = new FakeTransport()
                .enqueue(201, "{\"data\":{\"id\":\"cr1\",\"format\":\"carousel\",\"urlTags\":\"utm_source=meta\"}}")
                .enqueue(200, "{\"data\":{\"creatives\":[{\"id\":\"cr1\",\"name\":\"Spring\"}],\"workspaceId\":\"w1\"}}")
                .enqueue(201, "{\"data\":{\"id\":\"ad1\",\"creative\":{\"text\":\"Hi\",\"urlTags\":\"utm_source=meta\"}}}");

        FoPost client = TestSupport.client(transport);

        AdCreative creative = client.ads().createCreative(CreateAdCreativeParams.of(
                        "w1", "c1", "act_123", "page1", "Spring", "carousel", "New season")
                .urlTags("utm_source=meta")
                .card("https://cdn.yourbrand.com/1.jpg")
                .card("https://cdn.yourbrand.com/2.jpg", null, "Two", null));
        assertTrue(transport.lastBody().contains("\"urlTags\":\"utm_source=meta\""));
        assertTrue(transport.lastBody().contains(
                "\"cards\":[{\"mediaUrl\":\"https://cdn.yourbrand.com/1.jpg\"},"
                        + "{\"mediaUrl\":\"https://cdn.yourbrand.com/2.jpg\",\"headline\":\"Two\"}]"));
        assertEquals("utm_source=meta", creative.urlTags());

        List<AdCreative> creatives = client.ads().creatives("c1", "act_123");
        assertEquals("https://api.fopost.test/v1/ads/creatives?connection_id=c1&ad_account_id=act_123",
                transport.last().url());
        assertEquals("Spring", creatives.get(0).name());

        Ad ad = client.ads().create(CreateAdParams.of(
                        "w1", "c1", "act_123", "page1", "Spring", "traffic",
                        AdBudgetParams.daily(1000), AdTargetingParams.create(List.of("US"), 18, 65, "all"), "Hi")
                .urlTags("utm_source=meta"));
        assertTrue(transport.lastBody().contains("\"urlTags\":\"utm_source=meta\""));
        assertEquals("utm_source=meta", ad.creative().urlTags());
    }
}
