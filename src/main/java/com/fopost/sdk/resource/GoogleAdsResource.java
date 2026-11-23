package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.GoogleAdScheduleSlot;
import com.fopost.sdk.model.GoogleBidStrategy;
import com.fopost.sdk.model.GoogleConversionAction;
import com.fopost.sdk.model.GoogleLocalServicesLead;
import com.fopost.sdk.model.GoogleQueryResult;
import com.fopost.sdk.param.GoogleAdScheduleParams;
import com.fopost.sdk.param.GoogleAdsScope;
import com.fopost.sdk.param.GoogleBidStrategyParams;
import com.fopost.sdk.param.GoogleConversionParams;
import java.util.List;
import java.util.Map;

/**
 * The Google Ads surface no other network has: bid strategies, the ad schedule, Local Services
 * leads, conversions, and raw GAQL.
 *
 * <p>Ads, audiences and insights are on {@link AdsResource} and dispatch by connection; a
 * connection on another network answers 400 here. Every call needs the {@code ads} scope, and
 * anything that changes what a live account serves or bids also needs {@code publish}.
 * Amounts are in the account's currency, in minor units.
 *
 * <pre>{@code
 * var scope = GoogleAdsScope.of(connectionId, "1234567890");
 * for (var strategy : fopost.googleAds().bidStrategies(scope)) {
 *     System.out.println(strategy.name());
 * }
 * }</pre>
 */
public final class GoogleAdsResource {

    private final ApiClient http;

    public GoogleAdsResource(ApiClient http) {
        this.http = http;
    }

    // ── Bid strategies and ad schedule ──

    /** The account's portfolio bid strategies. */
    public List<GoogleBidStrategy> bidStrategies(GoogleAdsScope scope) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/google/bid-strategies", scope.toQuery())),
                GoogleBidStrategy.class);
    }

    /** Adds a bid strategy, which changes how campaigns bid. Needs {@code publish}. */
    public String createBidStrategy(GoogleBidStrategyParams params) {
        return id(http.post("/v1/ads/google/bid-strategies", params.toMap()));
    }

    /** A campaign's ad schedule. */
    public List<GoogleAdScheduleSlot> adSchedule(GoogleAdsScope scope, String campaignId) {
        Map<String, Object> query = scope.toQuery();
        query.put("campaign_id", campaignId);
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/google/ad-schedule", query)),
                GoogleAdScheduleSlot.class);
    }

    /**
     * Replaces every slot on the campaign: Google has no partial edit for a schedule, so a slot
     * left out stops serving. Answers how many landed. Needs {@code publish}.
     */
    public int setAdSchedule(GoogleAdScheduleParams params) {
        return ApiClient.unwrap(http.put("/v1/ads/google/ad-schedule", params.toMap()))
                .path("slots")
                .asInt();
    }

    // ── Local Services leads ──

    /** Leads from Local Services Ads, read live on every call and never stored by FoPost. */
    public List<GoogleLocalServicesLead> localServicesLeads(
            GoogleAdsScope scope, String since, String until) {
        Map<String, Object> query = scope.toQuery();
        query.put("since", since);
        query.put("until", until);
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/google/local-services", query)),
                GoogleLocalServicesLead.class);
    }

    // ── Conversions ──

    /** The account's conversion actions. */
    public List<GoogleConversionAction> conversionActions(GoogleAdsScope scope) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/google/conversions", scope.toQuery())),
                GoogleConversionAction.class);
    }

    /** Adds a conversion action. Needs {@code publish} as well as {@code ads}. */
    public String createConversionAction(GoogleConversionParams.CreateAction params) {
        return id(http.post("/v1/ads/google/conversions", params.toMap()));
    }

    /** Sends offline conversions; answers how many landed. Needs {@code publish}. */
    public int uploadConversions(GoogleConversionParams.Upload params) {
        return ApiClient.unwrap(http.post("/v1/ads/google/conversions/upload", params.toMap()))
                .path("uploaded")
                .asInt();
    }

    /** Sends conversion adjustments; answers how many landed. Needs {@code publish}. */
    public int uploadConversionAdjustments(GoogleConversionParams.Adjust params) {
        return ApiClient.unwrap(http.post("/v1/ads/google/conversions/adjustments", params.toMap()))
                .path("uploaded")
                .asInt();
    }

    // ── GAQL ──

    /**
     * Runs a read-only GAQL SELECT; rows come back exactly as Google sends them. The account read
     * is the scope's customer, never anything named inside the query text, and anything that is
     * not a SELECT is refused before the connection is touched.
     */
    public GoogleQueryResult query(GoogleAdsScope scope, String query) {
        Map<String, Object> body = scope.toMap();
        body.put("query", query);
        return http.convert(
                ApiClient.unwrap(http.post("/v1/ads/insights/query", body)), GoogleQueryResult.class);
    }

    private String id(com.fasterxml.jackson.databind.JsonNode response) {
        return ApiClient.unwrap(response).path("id").asText();
    }
}
