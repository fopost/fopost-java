package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.Ad;
import com.fopost.sdk.model.AudiencesResult;
import com.fopost.sdk.model.LeadsPage;
import com.fopost.sdk.param.AdBudgetParams;
import com.fopost.sdk.param.AdTargetingParams;
import com.fopost.sdk.param.BoostPostParams;
import com.fopost.sdk.param.CreateAudienceParams;
import com.fopost.sdk.param.CreateLeadFormParams;
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
}
