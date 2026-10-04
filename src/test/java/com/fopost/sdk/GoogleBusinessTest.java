package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.resource.GoogleBusinessResource;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

/**
 * Business Profile management: one call per route, pinning the URL, the verb
 * and the body each endpoint actually receives.
 */
class GoogleBusinessTest {

    private static final String BASE = "https://api.fopost.test/v1/accounts/a1/gbp";

    @Test
    void everyMethodMapsOntoItsRoute() {
        record Call(String method, String url, Consumer<GoogleBusinessResource> run) {}

        List<Call> calls = List.of(
                new Call("GET", BASE + "/location", gb -> gb.getLocation("a1")),
                new Call("PATCH", BASE + "/location", gb -> gb.updateLocation("a1", Map.of("title", "Bakery"))),
                new Call("GET", BASE + "/attributes", gb -> gb.getAttributes("a1")),
                new Call("PATCH", BASE + "/attributes", gb -> gb.updateAttributes("a1", List.of())),
                new Call("GET", BASE + "/menus", gb -> gb.getMenus("a1")),
                new Call("PUT", BASE + "/menus", gb -> gb.replaceMenus("a1", List.of())),
                new Call("GET", BASE + "/services", gb -> gb.getServices("a1")),
                new Call("PUT", BASE + "/services", gb -> gb.replaceServices("a1", List.of())),
                new Call("GET", BASE + "/media", gb -> gb.listMedia("a1")),
                new Call("POST", BASE + "/media", gb -> gb.addMedia("a1", "m1", null, null)),
                new Call("DELETE", BASE + "/media/CAoSL", gb -> gb.deleteMedia("a1", "CAoSL")),
                new Call("GET", BASE + "/place-actions", gb -> gb.listPlaceActions("a1")),
                new Call("POST", BASE + "/place-actions",
                        gb -> gb.createPlaceAction("a1", "https://example.test/book", "APPOINTMENT", null)),
                new Call("PATCH", BASE + "/place-actions/links-1",
                        gb -> gb.updatePlaceAction("a1", "links-1", null, true)),
                new Call("DELETE", BASE + "/place-actions/links-1",
                        gb -> gb.deletePlaceAction("a1", "links-1")),
                new Call("GET", BASE + "/verification", gb -> gb.getVerificationOptions("a1")),
                new Call("POST", BASE + "/verification/start",
                        gb -> gb.startVerification("a1", "SMS", null)),
                new Call("POST", BASE + "/verification/complete",
                        gb -> gb.completeVerification("a1", "v1", "123456")),
                new Call("POST", BASE + "/assign", gb -> gb.assign("a1", "w2")));

        for (Call call : calls) {
            FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":{\"ok\":true}}");
            call.run().accept(TestSupport.client(transport).googleBusiness());

            assertEquals(call.method(), transport.last().method(), call.url());
            assertEquals(call.url(), transport.last().url());
        }
    }

    @Test
    void aPatchCarriesOnlyTheFieldsTheCallerSet() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":{}}");

        TestSupport.client(transport)
                .googleBusiness()
                .updateLocation("a1", Map.of("store_code", "S-12"));

        assertEquals("{\"store_code\":\"S-12\"}", transport.lastBody());
    }

    @Test
    void aPhotoIsNamedByItsLibraryId() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":{}}");

        TestSupport.client(transport).googleBusiness().addMedia("a1", "m1", "INTERIOR", null);

        assertEquals("{\"media_id\":\"m1\",\"category\":\"INTERIOR\"}", transport.lastBody());
    }

    @Test
    void performanceRepeatsTheMetricParameter() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":{}}");

        TestSupport.client(transport)
                .googleBusiness()
                .getPerformance("a1", "2026-09-01", "2026-09-07", List.of("CALL_CLICKS", "WEBSITE_CLICKS"));

        assertEquals(
                BASE + "/performance?start_date=2026-09-01&end_date=2026-09-07"
                        + "&daily_metrics=CALL_CLICKS&daily_metrics=WEBSITE_CLICKS",
                transport.last().url());
    }

    @Test
    void searchKeywordsAsksTheSameRouteForTheMonthlyTerms() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":{}}");

        TestSupport.client(transport)
                .googleBusiness()
                .getSearchKeywords("a1", "2026-08-01", "2026-09-01", null);

        assertTrue(transport.last().url().contains("keywords=true"), transport.last().url());
    }

    @Test
    void aPendingApiGrantSurfacesAsAnError() {
        FakeTransport transport = new FakeTransport()
                .enqueue(503, "{\"error\":\"configuration_error\",\"message\":\"Not available yet\"}");

        FoPostException error = assertThrows(
                FoPostException.class,
                () -> TestSupport.client(transport).googleBusiness().getLocation("a1"));

        assertEquals(503, error.status());
        assertEquals("configuration_error", error.code());
    }
}
