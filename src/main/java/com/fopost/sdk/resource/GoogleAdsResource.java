package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.GoogleAdScheduleSlot;
import com.fopost.sdk.model.GoogleAssetGroup;
import com.fopost.sdk.model.GoogleAssetsResult;
import com.fopost.sdk.model.GoogleBidStrategy;
import com.fopost.sdk.model.GoogleConversionAction;
import com.fopost.sdk.model.GoogleKeyword;
import com.fopost.sdk.model.GoogleKeywordIdea;
import com.fopost.sdk.model.GoogleLocalServicesLead;
import com.fopost.sdk.model.GoogleOptimizationScore;
import com.fopost.sdk.model.GoogleQueryResult;
import com.fopost.sdk.model.GoogleRecommendation;
import com.fopost.sdk.model.GoogleSearchTerm;
import com.fopost.sdk.model.GoogleSharedSet;
import com.fopost.sdk.param.GoogleAdScheduleParams;
import com.fopost.sdk.param.GoogleAdsScope;
import com.fopost.sdk.param.GoogleAssetGroupParams;
import com.fopost.sdk.param.GoogleAssetParams;
import com.fopost.sdk.param.GoogleBidStrategyParams;
import com.fopost.sdk.param.GoogleConversionParams;
import com.fopost.sdk.param.GoogleKeywordIdeasParams;
import com.fopost.sdk.param.GoogleKeywordParams;
import com.fopost.sdk.param.GoogleNegativeKeywordParams;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * The Google Ads surface no other network has: recommendations, the optimization score, keywords,
 * assets, Performance Max asset groups, Local Services leads, conversions, and raw GAQL.
 *
 * <p>Campaigns, ad groups, ads, audiences and insights are on {@link AdsResource} and dispatch by
 * connection; a connection on another network answers 400 here. Every call needs the {@code ads}
 * scope, and anything that changes what a live account serves or bids also needs {@code publish}.
 * Amounts are in the account's currency, in minor units.
 *
 * <pre>{@code
 * var scope = GoogleAdsScope.of(connectionId, "1234567890");
 * for (var recommendation : fopost.googleAds().recommendations(scope)) {
 *     System.out.println(recommendation.type());
 * }
 * }</pre>
 */
public final class GoogleAdsResource {

    private final ApiClient http;

    public GoogleAdsResource(ApiClient http) {
        this.http = http;
    }

    // ── Recommendations ──

    /** Google's own read on what the account should change next. */
    public List<GoogleRecommendation> recommendations(GoogleAdsScope scope) {
        return recommendations(scope, List.of());
    }

    /** {@code types} narrows to those recommendation types, such as {@code KEYWORD}. */
    public List<GoogleRecommendation> recommendations(GoogleAdsScope scope, List<String> types) {
        Map<String, Object> query = scope.toQuery();
        if (!types.isEmpty()) {
            query.put("types", String.join(",", types));
        }
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/google/recommendations", query)),
                GoogleRecommendation.class);
    }

    /** The account's score and weight, and the score of each live campaign. */
    public GoogleOptimizationScore optimizationScore(GoogleAdsScope scope) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/ads/google/optimization-score", scope.toQuery())),
                GoogleOptimizationScore.class);
    }

    /**
     * Applies each one, which changes what the live account serves or bids, and answers how many
     * landed. Needs {@code publish} as well as {@code ads}. Each id has to name a recommendation
     * on this customer; any other answers 404.
     */
    public int applyRecommendations(GoogleAdsScope scope, List<String> ids) {
        return counted("/v1/ads/google/recommendations/apply", scope, ids, "applied");
    }

    /** Hides each one so Google stops surfacing it. Needs {@code publish}. */
    public int dismissRecommendations(GoogleAdsScope scope, List<String> ids) {
        return counted("/v1/ads/google/recommendations/dismiss", scope, ids, "dismissed");
    }

    // ── Keywords ──

    /** The keywords on the account. */
    public List<GoogleKeyword> keywords(GoogleAdsScope scope) {
        return keywords(scope, null);
    }

    /** The keywords on one ad group. */
    public List<GoogleKeyword> keywords(GoogleAdsScope scope, String adGroupId) {
        Map<String, Object> query = scope.toQuery();
        if (adGroupId != null) {
            query.put("ad_group_id", adGroupId);
        }
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/google/keywords", query)), GoogleKeyword.class);
    }

    /** Adds a keyword, which goes live. Needs {@code publish} as well as {@code ads}. */
    public String createKeyword(GoogleKeywordParams.Create params) {
        return id(http.post("/v1/ads/google/keywords", params.toMap()));
    }

    /** Pauses, resumes or rebids a keyword. Needs {@code publish}. */
    public String updateKeyword(String id, GoogleKeywordParams.Update params) {
        return id(http.patch("/v1/ads/google/keywords/" + encode(id), params.toMap()));
    }

    /** Removes a keyword. Needs {@code publish} as well as {@code ads}. */
    public void deleteKeyword(String id, GoogleAdsScope scope) {
        http.request("DELETE", "/v1/ads/google/keywords/" + encode(id), scope.toMap(), Map.of());
    }

    /** Ideas from seed keywords, a landing page, or both. */
    public List<GoogleKeywordIdea> keywordIdeas(GoogleKeywordIdeasParams params) {
        return http.convertList(
                ApiClient.unwrap(http.post("/v1/ads/google/keyword-ideas", params.toMap())),
                GoogleKeywordIdea.class);
    }

    /** Historical metrics for keywords you already have. */
    public List<GoogleKeywordIdea> keywordMetrics(GoogleAdsScope scope, List<String> keywords) {
        Map<String, Object> body = scope.toMap();
        body.put("keywords", keywords);
        return http.convertList(
                ApiClient.unwrap(http.post("/v1/ads/google/keyword-metrics", body)),
                GoogleKeywordIdea.class);
    }

    /** What people actually searched, with the metrics each term earned. */
    public List<GoogleSearchTerm> searchTerms(GoogleAdsScope scope, String since, String until) {
        Map<String, Object> query = scope.toQuery();
        query.put("since", since);
        query.put("until", until);
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/google/search-terms", query)),
                GoogleSearchTerm.class);
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

    // ── Negative keyword lists ──

    /** The account's negative keyword lists. */
    public List<GoogleSharedSet> negativeKeywordLists(GoogleAdsScope scope) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/google/negative-keywords", scope.toQuery())),
                GoogleSharedSet.class);
    }

    /** Creates a negative keyword list. Needs {@code publish} as well as {@code ads}. */
    public String createNegativeKeywordList(GoogleAdsScope scope, String name) {
        Map<String, Object> body = scope.toMap();
        body.put("name", name);
        return id(http.post("/v1/ads/google/negative-keywords", body));
    }

    /** Adds keywords to a list; answers how many landed. Needs {@code publish}. */
    public int addNegativeKeywords(GoogleNegativeKeywordParams params) {
        return ApiClient.unwrap(http.post("/v1/ads/google/negative-keywords/keywords", params.toMap()))
                .path("added")
                .asInt();
    }

    /** Puts a list on a campaign, which stops it matching those terms. Needs {@code publish}. */
    public void attachNegativeKeywordList(
            GoogleAdsScope scope, String sharedSetId, String campaignId) {
        Map<String, Object> body = scope.toMap();
        body.put("sharedSetId", sharedSetId);
        body.put("campaignId", campaignId);
        http.post("/v1/ads/google/negative-keywords/attach", body);
    }

    // ── Assets ──

    /** Sitelinks, callouts and snippets, with the links that place each one. */
    public GoogleAssetsResult assets(GoogleAdsScope scope) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/ads/google/assets", scope.toQuery())),
                GoogleAssetsResult.class);
    }

    /** Adds an asset to the library. Needs {@code publish} as well as {@code ads}. */
    public String createAsset(GoogleAssetParams params) {
        return id(http.post("/v1/ads/google/assets", params.toMap()));
    }

    /**
     * Puts an asset under the ads it belongs to, which changes what they render. Attaches to the
     * account when {@code campaignId} is null. Needs {@code publish}.
     */
    public void attachAsset(
            GoogleAdsScope scope, String assetId, String fieldType, String campaignId) {
        Map<String, Object> body = scope.toMap();
        body.put("assetId", assetId);
        body.put("fieldType", fieldType);
        if (campaignId != null) {
            body.put("campaignId", campaignId);
        }
        http.post("/v1/ads/google/assets/attach", body);
    }

    /**
     * Removes the links that put an asset under an ad; on Google the asset itself is permanent.
     * Needs {@code publish} as well as {@code ads}.
     */
    public void deleteAsset(String id, GoogleAdsScope scope) {
        http.request("DELETE", "/v1/ads/google/assets/" + encode(id), scope.toMap(), Map.of());
    }

    // ── Performance Max asset groups ──

    /** The account's Performance Max asset groups. */
    public List<GoogleAssetGroup> assetGroups(GoogleAdsScope scope) {
        return assetGroups(scope, null);
    }

    /** One campaign's Performance Max asset groups. */
    public List<GoogleAssetGroup> assetGroups(GoogleAdsScope scope, String campaignId) {
        Map<String, Object> query = scope.toQuery();
        if (campaignId != null) {
            query.put("campaign_id", campaignId);
        }
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/google/asset-groups", query)),
                GoogleAssetGroup.class);
    }

    /** Creates an asset group. It starts paused unless the status says otherwise. Needs {@code publish}. */
    public String createAssetGroup(GoogleAssetGroupParams.Create params) {
        return id(http.post("/v1/ads/google/asset-groups", params.toMap()));
    }

    /** Renames, pauses or resumes an asset group. Needs {@code publish}. */
    public String updateAssetGroup(String id, GoogleAssetGroupParams.Update params) {
        return id(http.patch("/v1/ads/google/asset-groups/" + encode(id), params.toMap()));
    }

    /** Removes an asset group. Needs {@code publish} as well as {@code ads}. */
    public void deleteAssetGroup(String id, GoogleAdsScope scope) {
        http.request("DELETE", "/v1/ads/google/asset-groups/" + encode(id), scope.toMap(), Map.of());
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

    private int counted(String path, GoogleAdsScope scope, List<String> ids, String key) {
        Map<String, Object> body = scope.toMap();
        body.put("ids", ids);
        return ApiClient.unwrap(http.post(path, body)).path(key).asInt();
    }

    private String id(com.fasterxml.jackson.databind.JsonNode response) {
        return ApiClient.unwrap(response).path("id").asText();
    }

    private static String encode(String id) {
        return URLEncoder.encode(id, StandardCharsets.UTF_8);
    }
}
