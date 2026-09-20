package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.AdProvider;
import com.fopost.sdk.param.AdCompanyParams;
import com.fopost.sdk.param.ConversionEventParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdsNetworksTest {

    @Test
    void authorizeReachesWhicheverNetworkTheRegistryNamed() {
        FakeTransport transport =
                new FakeTransport().enqueue(200, "{\"data\":{\"url\":\"https://www.linkedin.com/oauth\"}}");

        String url = TestSupport.client(transport).ads().authorize("linkedin", "w1", null, "/ads");

        assertEquals("https://api.fopost.test/v1/ads/connections/linkedin/authorize", transport.last().url());
        assertEquals("{\"workspaceId\":\"w1\",\"returnTo\":\"/ads\"}", transport.lastBody());
        assertEquals("https://www.linkedin.com/oauth", url);
    }

    @Test
    void providersCarryWhatEachNetworkSupports() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"linkedin","name":"LinkedIn Ads","logo":"linkedin","configured":false,
                                  "connectMethods":[],"capabilities":{"conversions":true},
                                  "targetingFacets":["country","job_title"],
                                  "trackingMacros":[{"token":"{{LINKEDIN_CAMPAIGN_ID}}","description":"Campaign"}]}]}""");

        List<AdProvider> providers = TestSupport.client(transport).ads().providers();

        assertEquals(1, providers.size());
        assertFalse(providers.get(0).configured());
        assertTrue(providers.get(0).capabilities().get("conversions"));
        assertEquals(List.of("country", "job_title"), providers.get(0).targetingFacets());
        assertEquals("{{LINKEDIN_CAMPAIGN_ID}}", providers.get(0).trackingMacros().get(0).token());
    }

    @Test
    void companyRowsTravelWithTheRequest() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":{\"added\":2}}");

        int added = TestSupport.client(transport)
                .ads()
                .addAudienceCompanies(
                        "urn:li:adSegment:44",
                        "w1",
                        "c1",
                        List.of(AdCompanyParams.domain("northwind.example"), AdCompanyParams.named("Contoso")));

        assertEquals(2, added);
        assertEquals(
                "{\"companies\":[{\"domain\":\"northwind.example\"},{\"name\":\"Contoso\"}]}",
                transport.lastBody());
    }

    @Test
    void conversionEventsSendTheIdentityTheApiHashes() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":{\"accepted\":1}}");

        int accepted = TestSupport.client(transport)
                .ads()
                .sendConversionEvents(
                        "urn:li:conversion:9",
                        "w1",
                        "c1",
                        List.of(ConversionEventParams.at(1758326400000L).email("buyer@example.test")));

        assertEquals(1, accepted);
        assertTrue(transport
                .last()
                .url()
                .startsWith("https://api.fopost.test/v1/ads/linkedin/conversion-rules/urn:li:conversion:9/events"));
    }
}
