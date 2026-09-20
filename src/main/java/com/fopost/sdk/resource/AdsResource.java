package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Ad;
import com.fopost.sdk.model.AdActivity;
import com.fopost.sdk.model.AdLabel;
import com.fopost.sdk.model.AdLibraryPage;
import com.fopost.sdk.model.AdStudy;
import com.fopost.sdk.model.CatalogBatchResult;
import com.fopost.sdk.model.CatalogProductsPage;
import com.fopost.sdk.model.HighDemandPeriod;
import com.fopost.sdk.model.IosCampaignLimits;
import com.fopost.sdk.model.PartnershipCreator;
import com.fopost.sdk.model.ProductCatalog;
import com.fopost.sdk.model.ProductFeed;
import com.fopost.sdk.model.ProductFeedUpload;
import com.fopost.sdk.model.ProductSet;
import com.fopost.sdk.model.ReachFrequencyPrediction;
import com.fopost.sdk.model.ValueRuleSet;
import com.fopost.sdk.param.AdLabelParams;
import com.fopost.sdk.param.ApplyAdLabelParams;
import com.fopost.sdk.param.CatalogProductBatchParams;
import com.fopost.sdk.param.CreateAdStudyParams;
import com.fopost.sdk.param.CreateCatalogParams;
import com.fopost.sdk.param.CreateHighDemandPeriodParams;
import com.fopost.sdk.param.CreateProductFeedParams;
import com.fopost.sdk.param.CreateReachFrequencyParams;
import com.fopost.sdk.param.CreateValueRuleSetParams;
import com.fopost.sdk.param.PartnershipParams;
import com.fopost.sdk.param.ProductSetParams;
import com.fopost.sdk.param.ReachFrequencyActionParams;
import com.fopost.sdk.param.StartFeedUploadParams;
import com.fopost.sdk.param.UpdateCatalogParams;
import com.fopost.sdk.model.AdAccountTree;
import com.fopost.sdk.model.AdCampaign;
import com.fopost.sdk.model.AdConnection;
import com.fopost.sdk.model.AdCreative;
import com.fopost.sdk.model.AdInsightsReport;
import com.fopost.sdk.model.AdSet;
import com.fopost.sdk.model.AdSource;
import com.fopost.sdk.model.Audience;
import com.fopost.sdk.model.AudiencesResult;
import com.fopost.sdk.model.BoostablePost;
import com.fopost.sdk.model.BulkAdStatusResult;
import com.fopost.sdk.model.CreatedAudience;
import com.fopost.sdk.model.ExternalAd;
import com.fopost.sdk.model.LeadFormDetail;
import com.fopost.sdk.model.LeadFormSource;
import com.fopost.sdk.model.LeadPage;
import com.fopost.sdk.model.LeadPageSubscription;
import com.fopost.sdk.model.LeadsFeed;
import com.fopost.sdk.model.LeadsPage;
import com.fopost.sdk.model.NetworkAd;
import com.fopost.sdk.model.ReachEstimate;
import com.fopost.sdk.model.TargetingOption;
import com.fopost.sdk.param.AdInsightsParams;
import com.fopost.sdk.param.BoostPostParams;
import com.fopost.sdk.param.BulkAdStatusParams;
import com.fopost.sdk.param.CreateAdCampaignParams;
import com.fopost.sdk.param.CreateAdCreativeParams;
import com.fopost.sdk.param.CreateAdParams;
import com.fopost.sdk.param.CreateAdSetParams;
import com.fopost.sdk.param.CreateAudienceParams;
import com.fopost.sdk.param.CreateLeadFormParams;
import com.fopost.sdk.param.CreateNetworkAdParams;
import com.fopost.sdk.param.LeadsFeedParams;
import com.fopost.sdk.param.ReachEstimateParams;
import com.fopost.sdk.param.UpdateAdCampaignParams;
import com.fopost.sdk.param.UpdateAdSetParams;
import com.fopost.sdk.param.UpdateAudienceParams;
import com.fopost.sdk.param.UpdateNetworkAdParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Boosts, ads, campaigns, creatives, catalogs, audiences, predictions, the public ad archive,
 * insights and lead forms on the connected ad accounts.
 *
 * <pre>{@code
 * BoostablePost candidate = client.ads().boostable(workspaceId).get(0);
 * Ad boost = client.ads().boost(BoostPostParams.of(
 *         workspaceId, connectionId, "act_123", candidate.id(), candidate.deliveries().get(0).accountId(),
 *         "Launch week", "engagement",
 *         AdBudgetParams.daily(2000),
 *         AdTargetingParams.create(List.of("US"), 21, 45, "all")));
 *
 * client.ads().setStatus(boost.id(), workspaceId, "active");
 * }</pre>
 *
 * <p>Every call needs the {@code ads} scope on an API key. {@link #boost}, {@link #create},
 * {@link #setStatus} and {@link #delete} spend money and also need the {@code publish} scope, as do
 * creating, updating, deleting and duplicating campaigns, ad sets and network ads, and
 * {@link #bulkSetStatus}. A
 * boost or ad starts paused unless {@code paused} is set to false, so nothing is spent until it is
 * resumed.
 */
public final class AdsResource {

    private final ApiClient http;

    public AdsResource(ApiClient http) {
        this.http = http;
    }

    // ─── Reading ──────────────────────────────────────────────────────────────

    public List<Ad> list() {
        return list(null);
    }

    /** Boosts and ads created through FoPost, with the insights from their last refresh. */
    public List<Ad> list(String workspaceId) {
        return http.convertList(ApiClient.unwrap(http.get("/v1/ads", workspaceQuery(workspaceId))), Ad.class);
    }

    public List<ExternalAd> external() {
        return external(null);
    }

    /** Ads on the connected ad accounts that were made elsewhere. Read live, never stored. */
    public List<ExternalAd> external(String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/external", workspaceQuery(workspaceId))), ExternalAd.class);
    }

    public List<BoostablePost> boostable() {
        return boostable(null);
    }

    /** Published posts with a delivery on an account an ads connection reaches. */
    public List<BoostablePost> boostable(String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/boostable", workspaceQuery(workspaceId))), BoostablePost.class);
    }

    public List<AdConnection> connections() {
        return connections(null);
    }

    public List<AdConnection> connections(String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/connections", workspaceQuery(workspaceId))), AdConnection.class);
    }

    public List<AdSource> sources() {
        return sources(null);
    }

    /** Each connection with the ad accounts and Pages its grant reaches. */
    public List<AdSource> sources(String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/sources", workspaceQuery(workspaceId))), AdSource.class);
    }

    // ─── Connections ──────────────────────────────────────────────────────────

    public String authorizeMeta(String workspaceId) {
        return authorizeMeta(workspaceId, null, null);
    }

    /**
     * The login url for connecting a Meta Ads account. The user who calls this must finish the
     * login in their own browser session. {@code method} is business or user; {@code returnTo} is
     * the dashboard path to land on afterwards.
     */
    public String authorizeMeta(String workspaceId, String method, String returnTo) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspaceId", workspaceId);
        if (method != null) {
            body.put("method", method);
        }
        if (returnTo != null) {
            body.put("returnTo", returnTo);
        }
        return ApiClient.unwrap(http.post("/v1/ads/connections/meta/authorize", body)).path("url").asText();
    }

    /** Also deletes every ad record FoPost created through the connection. */
    public void deleteConnection(String connectionId, String workspaceId) {
        http.request("DELETE", "/v1/ads/connections/" + connectionId, null, workspaceQuery(workspaceId));
    }

    // ─── Spending ─────────────────────────────────────────────────────────────

    /**
     * Promote a post FoPost already published. Needs the {@code publish} scope as well as
     * {@code ads}. Starts paused unless {@code paused} is false.
     */
    public Ad boost(BoostPostParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/ads/boost", params.toMap())), Ad.class);
    }

    /**
     * Create a campaign, ad set and ad from a creative. Needs the {@code publish} scope as well as
     * {@code ads}. Starts paused unless {@code paused} is false.
     */
    public Ad create(CreateAdParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/ads", params.toMap())), Ad.class);
    }

    /** Read the delivery status and lifetime insights from the ad account and store them. */
    public Ad refresh(String adId, String workspaceId) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/ads/" + adId + "/refresh", null, workspaceQuery(workspaceId))),
                Ad.class);
    }

    /** {@code status} is active or paused. Needs the {@code publish} scope as well as {@code ads}. */
    public Ad setStatus(String adId, String workspaceId, String status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", "/v1/ads/" + adId, body, workspaceQuery(workspaceId))),
                Ad.class);
    }

    /**
     * End delivery and delete the ad on the ad account as well as here. Needs the {@code publish}
     * scope as well as {@code ads}.
     */
    public void delete(String adId, String workspaceId) {
        http.request("DELETE", "/v1/ads/" + adId, null, workspaceQuery(workspaceId));
    }

    // ─── Campaigns, ad sets and ads ───────────────────────────────────────────

    public AdAccountTree accountTree(String adAccountId, String connectionId) {
        return accountTree(adAccountId, connectionId, null);
    }

    /** Every campaign on the ad account with its ad sets and ads, read live from Meta. */
    public AdAccountTree accountTree(String adAccountId, String connectionId, String workspaceId) {
        return http.convert(
                ApiClient.unwrap(http.get(
                        "/v1/ads/accounts/" + adAccountId + "/tree", connectionQuery(workspaceId, connectionId))),
                AdAccountTree.class);
    }

    /** Needs the {@code publish} scope as well as {@code ads}. Starts paused unless {@code paused} is false. */
    public AdCampaign createCampaign(CreateAdCampaignParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/ads/campaigns", params.toMap())), AdCampaign.class);
    }

    public AdCampaign campaign(String campaignId, String connectionId) {
        return campaign(campaignId, connectionId, null);
    }

    public AdCampaign campaign(String campaignId, String connectionId, String workspaceId) {
        return http.convert(
                ApiClient.unwrap(
                        http.get("/v1/ads/campaigns/" + campaignId, connectionQuery(workspaceId, connectionId))),
                AdCampaign.class);
    }

    /** Needs the {@code publish} scope as well as {@code ads}. */
    public AdCampaign updateCampaign(
            String campaignId, String workspaceId, String connectionId, UpdateAdCampaignParams params) {
        return http.convert(
                ApiClient.unwrap(http.request(
                        "PATCH",
                        "/v1/ads/campaigns/" + campaignId,
                        params.toMap(),
                        connectionQuery(workspaceId, connectionId))),
                AdCampaign.class);
    }

    /** Deletes the campaign with its ad sets and ads. Needs the {@code publish} scope as well as {@code ads}. */
    public void deleteCampaign(String campaignId, String workspaceId, String connectionId) {
        http.request("DELETE", "/v1/ads/campaigns/" + campaignId, null, connectionQuery(workspaceId, connectionId));
    }

    public String duplicateCampaign(String campaignId, String workspaceId, String connectionId) {
        return duplicateCampaign(campaignId, workspaceId, connectionId, null);
    }

    /**
     * Copy the campaign and everything in it. Returns the copy's Meta id. The copy starts paused
     * unless {@code paused} is false. Needs the {@code publish} scope as well as {@code ads}.
     */
    public String duplicateCampaign(String campaignId, String workspaceId, String connectionId, Boolean paused) {
        return duplicate("/v1/ads/campaigns/" + campaignId, workspaceId, connectionId, paused);
    }

    /** Needs the {@code publish} scope as well as {@code ads}. Starts paused unless {@code paused} is false. */
    public AdSet createAdSet(CreateAdSetParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/ads/ad-sets", params.toMap())), AdSet.class);
    }

    public AdSet adSet(String adSetId, String connectionId) {
        return adSet(adSetId, connectionId, null);
    }

    public AdSet adSet(String adSetId, String connectionId, String workspaceId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/ads/ad-sets/" + adSetId, connectionQuery(workspaceId, connectionId))),
                AdSet.class);
    }

    /** Needs the {@code publish} scope as well as {@code ads}. */
    public AdSet updateAdSet(String adSetId, String workspaceId, String connectionId, UpdateAdSetParams params) {
        return http.convert(
                ApiClient.unwrap(http.request(
                        "PATCH",
                        "/v1/ads/ad-sets/" + adSetId,
                        params.toMap(),
                        connectionQuery(workspaceId, connectionId))),
                AdSet.class);
    }

    /** Needs the {@code publish} scope as well as {@code ads}. */
    public void deleteAdSet(String adSetId, String workspaceId, String connectionId) {
        http.request("DELETE", "/v1/ads/ad-sets/" + adSetId, null, connectionQuery(workspaceId, connectionId));
    }

    public String duplicateAdSet(String adSetId, String workspaceId, String connectionId) {
        return duplicateAdSet(adSetId, workspaceId, connectionId, null);
    }

    /** Returns the copy's Meta id. Needs the {@code publish} scope as well as {@code ads}. */
    public String duplicateAdSet(String adSetId, String workspaceId, String connectionId, Boolean paused) {
        return duplicate("/v1/ads/ad-sets/" + adSetId, workspaceId, connectionId, paused);
    }

    /**
     * An ad inside an ad set, unlike {@link #create}, which builds a whole campaign. Needs the
     * {@code publish} scope as well as {@code ads}. Starts paused unless {@code paused} is false.
     */
    public NetworkAd createNetworkAd(CreateNetworkAdParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/ads/ads", params.toMap())), NetworkAd.class);
    }

    public NetworkAd networkAd(String adId, String connectionId) {
        return networkAd(adId, connectionId, null);
    }

    /** {@code adId} is Meta's ad id. */
    public NetworkAd networkAd(String adId, String connectionId, String workspaceId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/ads/ads/" + adId, connectionQuery(workspaceId, connectionId))),
                NetworkAd.class);
    }

    /** Needs the {@code publish} scope as well as {@code ads}. */
    public NetworkAd updateNetworkAd(
            String adId, String workspaceId, String connectionId, UpdateNetworkAdParams params) {
        return http.convert(
                ApiClient.unwrap(http.request(
                        "PATCH", "/v1/ads/ads/" + adId, params.toMap(), connectionQuery(workspaceId, connectionId))),
                NetworkAd.class);
    }

    /** Needs the {@code publish} scope as well as {@code ads}. */
    public void deleteNetworkAd(String adId, String workspaceId, String connectionId) {
        http.request("DELETE", "/v1/ads/ads/" + adId, null, connectionQuery(workspaceId, connectionId));
    }

    public String duplicateNetworkAd(String adId, String workspaceId, String connectionId) {
        return duplicateNetworkAd(adId, workspaceId, connectionId, null);
    }

    /** Returns the copy's Meta id. Needs the {@code publish} scope as well as {@code ads}. */
    public String duplicateNetworkAd(String adId, String workspaceId, String connectionId, Boolean paused) {
        return duplicate("/v1/ads/ads/" + adId, workspaceId, connectionId, paused);
    }

    /**
     * Pause or resume campaigns, ad sets and ads in one call; each object reports on its own.
     * Needs the {@code publish} scope as well as {@code ads}.
     */
    public List<BulkAdStatusResult> bulkSetStatus(BulkAdStatusParams params) {
        return http.convertList(
                ApiClient.unwrap(http.post("/v1/ads/status", params.toMap())), BulkAdStatusResult.class);
    }

    // ─── Creatives ────────────────────────────────────────────────────────────

    public List<AdCreative> creatives(String connectionId, String adAccountId) {
        return creatives(connectionId, adAccountId, null);
    }

    /** The creatives on one ad account. */
    public List<AdCreative> creatives(String connectionId, String adAccountId, String workspaceId) {
        Map<String, Object> query = connectionQuery(workspaceId, connectionId);
        query.put("ad_account_id", adAccountId);
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/creatives", query)).path("creatives"), AdCreative.class);
    }

    public AdCreative createCreative(CreateAdCreativeParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/ads/creatives", params.toMap())), AdCreative.class);
    }

    public AdCreative creative(String creativeId, String connectionId) {
        return creative(creativeId, connectionId, null);
    }

    public AdCreative creative(String creativeId, String connectionId, String workspaceId) {
        return http.convert(
                ApiClient.unwrap(
                        http.get("/v1/ads/creatives/" + creativeId, connectionQuery(workspaceId, connectionId))),
                AdCreative.class);
    }

    public void deleteCreative(String creativeId, String workspaceId, String connectionId) {
        http.request("DELETE", "/v1/ads/creatives/" + creativeId, null, connectionQuery(workspaceId, connectionId));
    }

    // ─── Reach and insights ───────────────────────────────────────────────────

    /** The audience size a targeting would reach. */
    public ReachEstimate estimateReach(ReachEstimateParams params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/ads/reach-estimate", params.toMap())), ReachEstimate.class);
    }

    public AdInsightsReport insights(String connectionId, String objectId, AdInsightsParams params) {
        return insights(connectionId, objectId, params, null);
    }

    /** Insights for any campaign, ad set or ad on the ad account, by its Meta id. */
    public AdInsightsReport insights(
            String connectionId, String objectId, AdInsightsParams params, String workspaceId) {
        Map<String, Object> query = connectionQuery(workspaceId, connectionId);
        query.put("object_id", objectId);
        query.putAll(params.toQuery());
        return http.convert(ApiClient.unwrap(http.get("/v1/ads/insights", query)), AdInsightsReport.class);
    }

    /** Insights for a boost or ad created through FoPost, by its FoPost id. */
    public AdInsightsReport adInsights(String adId, String workspaceId, AdInsightsParams params) {
        Map<String, Object> query = workspaceQuery(workspaceId);
        query.putAll(params.toQuery());
        return http.convert(
                ApiClient.unwrap(http.get("/v1/ads/" + adId + "/insights", query)), AdInsightsReport.class);
    }

    // ─── Audiences and targeting ──────────────────────────────────────────────

    public AudiencesResult audiences(String connectionId, String adAccountId) {
        return audiences(connectionId, adAccountId, null);
    }

    /** The saved audiences and pixels on one ad account. */
    public AudiencesResult audiences(String connectionId, String adAccountId, String workspaceId) {
        Map<String, Object> query = workspaceQuery(workspaceId);
        query.put("connection_id", connectionId);
        query.put("ad_account_id", adAccountId);
        return http.convert(ApiClient.unwrap(http.get("/v1/ads/audiences", query)), AudiencesResult.class);
    }

    public CreatedAudience createAudience(CreateAudienceParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/ads/audiences", params.toMap())), CreatedAudience.class);
    }

    public Audience audience(String audienceId, String connectionId) {
        return audience(audienceId, connectionId, null);
    }

    public Audience audience(String audienceId, String connectionId, String workspaceId) {
        return http.convert(
                ApiClient.unwrap(
                        http.get("/v1/ads/audiences/" + audienceId, connectionQuery(workspaceId, connectionId))),
                Audience.class);
    }

    public Audience updateAudience(
            String audienceId, String workspaceId, String connectionId, UpdateAudienceParams params) {
        return http.convert(
                ApiClient.unwrap(http.request(
                        "PATCH",
                        "/v1/ads/audiences/" + audienceId,
                        params.toMap(),
                        connectionQuery(workspaceId, connectionId))),
                Audience.class);
    }

    public void deleteAudience(String audienceId, String workspaceId, String connectionId) {
        http.request("DELETE", "/v1/ads/audiences/" + audienceId, null, connectionQuery(workspaceId, connectionId));
    }

    /** Add people to a custom audience; the emails are hashed before they leave the API. Returns the count sent. */
    public int addAudienceUsers(String audienceId, String workspaceId, String connectionId, List<String> emails) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("emails", List.copyOf(emails));
        Map<String, Object> query = connectionQuery(workspaceId, connectionId);
        return ApiClient.unwrap(http.post("/v1/ads/audiences/" + audienceId + "/users", body, query))
                .path("added")
                .asInt();
    }

    public List<TargetingOption> searchTargeting(String connectionId, String type, String q) {
        return searchTargeting(connectionId, type, q, null);
    }

    /**
     * Locations, interests, behaviours and income brackets as the ads platform names them, for
     * {@link com.fopost.sdk.param.AdTargetingParams}. {@code type} is country, region, city, zip,
     * metro, interest, behavior or income; {@code income} ignores {@code q}.
     */
    public List<TargetingOption> searchTargeting(String connectionId, String type, String q, String workspaceId) {
        Map<String, Object> query = workspaceQuery(workspaceId);
        query.put("connection_id", connectionId);
        query.put("type", type);
        if (q != null) {
            query.put("q", q);
        }
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/targeting/search", query)), TargetingOption.class);
    }

    // ─── Lead forms ───────────────────────────────────────────────────────────

    public List<LeadFormSource> leadForms() {
        return leadForms(null);
    }

    /** Every Page an ads connection reaches, with its lead forms. */
    public List<LeadFormSource> leadForms(String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/lead-forms", workspaceQuery(workspaceId))), LeadFormSource.class);
    }

    /** Create an instant form on the Page. Returns its id. */
    public String createLeadForm(CreateLeadFormParams params) {
        return ApiClient.unwrap(http.post("/v1/ads/lead-forms", params.toMap())).path("id").asText();
    }

    public LeadsPage leads(String formId, String connectionId, String pageId) {
        return leads(formId, connectionId, pageId, null, null);
    }

    /** One page of leads; pass {@code nextCursor} back as {@code after} for the next. */
    public LeadsPage leads(String formId, String connectionId, String pageId, String after, String workspaceId) {
        Map<String, Object> query = workspaceQuery(workspaceId);
        query.put("connection_id", connectionId);
        query.put("page_id", pageId);
        if (after != null) {
            query.put("after", after);
        }
        return http.convert(
                ApiClient.unwrap(http.get("/v1/ads/lead-forms/" + formId + "/leads", query)), LeadsPage.class);
    }

    public LeadFormDetail leadForm(String formId, String connectionId, String pageId) {
        return leadForm(formId, connectionId, pageId, null);
    }

    public LeadFormDetail leadForm(String formId, String connectionId, String pageId, String workspaceId) {
        Map<String, Object> query = connectionQuery(workspaceId, connectionId);
        query.put("page_id", pageId);
        return http.convert(ApiClient.unwrap(http.get("/v1/ads/lead-forms/" + formId, query)), LeadFormDetail.class);
    }

    /** Stop the form taking new leads. Returns it as archived. */
    public LeadFormDetail archiveLeadForm(String formId, String workspaceId, String connectionId, String pageId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspaceId", workspaceId);
        body.put("connectionId", connectionId);
        body.put("pageId", pageId);
        return http.convert(
                ApiClient.unwrap(http.post("/v1/ads/lead-forms/" + formId + "/archive", body)), LeadFormDetail.class);
    }

    public LeadsFeed leadsFeed() {
        return leadsFeed(LeadsFeedParams.create());
    }

    /**
     * Leads stored from the subscribed Pages, newest first. Pass {@code nextCursor} back as
     * {@code cursor} for the next page.
     */
    public LeadsFeed leadsFeed(LeadsFeedParams params) {
        return http.convert(ApiClient.unwrap(http.get("/v1/ads/leads", params.toQuery())), LeadsFeed.class);
    }

    public List<LeadPage> leadPages() {
        return leadPages(null);
    }

    /** The Pages whose new leads FoPost stores as they arrive. */
    public List<LeadPage> leadPages(String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/lead-pages", workspaceQuery(workspaceId))), LeadPage.class);
    }

    /** Start storing a Page's leads as they arrive; its existing leads are backfilled. */
    public LeadPageSubscription subscribeLeadPage(String workspaceId, String connectionId, String pageId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspaceId", workspaceId);
        body.put("connectionId", connectionId);
        body.put("pageId", pageId);
        return http.convert(ApiClient.unwrap(http.post("/v1/ads/lead-pages", body)), LeadPageSubscription.class);
    }

    public void unsubscribeLeadPage(String pageId, String workspaceId, String connectionId) {
        http.request("DELETE", "/v1/ads/lead-pages/" + pageId, null, connectionQuery(workspaceId, connectionId));
    }

    // ─── Plumbing ─────────────────────────────────────────────────────────────

    private String duplicate(String path, String workspaceId, String connectionId, Boolean paused) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (paused != null) {
            body.put("paused", paused);
        }
        return ApiClient.unwrap(http.post(path + "/duplicate", body, connectionQuery(workspaceId, connectionId)))
                .path("id")
                .asText();
    }

    // ─── Goals ──────────────────────────────────────────────────────

    /**
     * The goals this connection's network can run right now. Ask rather than assume: a goal the
     * deployment is not set up for is absent here and is refused if you send it anyway.
     */
    public List<String> goals(String connectionId) {
        return goals(connectionId, null);
    }

    public List<String> goals(String connectionId, String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/goals", connectionQuery(workspaceId, connectionId))),
                String.class);
    }

    // ─── Product catalogs ───────────────────────────────────────────

    /** Catalogs the connection's business portfolios reach. Read live, never stored. */
    public List<ProductCatalog> catalogs(String connectionId) {
        return catalogs(connectionId, null);
    }

    public List<ProductCatalog> catalogs(String connectionId, String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/catalogs", connectionQuery(workspaceId, connectionId)))
                        .path("catalogs"),
                ProductCatalog.class);
    }

    /** Created on the connection's business portfolio. Also needs the {@code publish} scope. */
    public ProductCatalog createCatalog(CreateCatalogParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/ads/catalogs", params.toMap())), ProductCatalog.class);
    }

    public ProductCatalog catalog(String catalogId, String connectionId) {
        return catalog(catalogId, connectionId, null);
    }

    public ProductCatalog catalog(String catalogId, String connectionId, String workspaceId) {
        return http.convert(
                ApiClient.unwrap(
                        http.get("/v1/ads/catalogs/" + catalogId, connectionQuery(workspaceId, connectionId))),
                ProductCatalog.class);
    }

    /** Also needs the {@code publish} scope. */
    public ProductCatalog updateCatalog(
            String catalogId, String workspaceId, String connectionId, UpdateCatalogParams params) {
        return http.convert(
                ApiClient.unwrap(http.request(
                        "PATCH",
                        "/v1/ads/catalogs/" + catalogId,
                        params.toMap(),
                        connectionQuery(workspaceId, connectionId))),
                ProductCatalog.class);
    }

    /**
     * Deletes the catalog with every product, feed and set in it. Also needs the {@code publish}
     * scope.
     */
    public void deleteCatalog(String catalogId, String workspaceId, String connectionId) {
        http.request("DELETE", "/v1/ads/catalogs/" + catalogId, null, connectionQuery(workspaceId, connectionId));
    }

    /** One page of products; pass {@code nextCursor} back as {@code after}. */
    public CatalogProductsPage catalogProducts(String catalogId, String connectionId) {
        return catalogProducts(catalogId, connectionId, null, null);
    }

    public CatalogProductsPage catalogProducts(
            String catalogId, String connectionId, String workspaceId, String after) {
        Map<String, Object> query = connectionQuery(workspaceId, connectionId);
        if (after != null) {
            query.put("after", after);
        }
        return http.convert(
                ApiClient.unwrap(http.get("/v1/ads/catalogs/" + catalogId + "/products", query)),
                CatalogProductsPage.class);
    }

    /**
     * Up to 500 upserts and deletes in one batch, keyed by your own retailer id. Also needs the
     * {@code publish} scope.
     */
    public CatalogBatchResult writeCatalogProducts(String catalogId, CatalogProductBatchParams params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/ads/catalogs/" + catalogId + "/products", params.toMap())),
                CatalogBatchResult.class);
    }

    public List<ProductFeed> productFeeds(String catalogId, String connectionId) {
        return productFeeds(catalogId, connectionId, null);
    }

    public List<ProductFeed> productFeeds(String catalogId, String connectionId, String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get(
                        "/v1/ads/catalogs/" + catalogId + "/feeds", connectionQuery(workspaceId, connectionId))),
                ProductFeed.class);
    }

    /** Also needs the {@code publish} scope. */
    public ProductFeed createProductFeed(String catalogId, CreateProductFeedParams params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/ads/catalogs/" + catalogId + "/feeds", params.toMap())),
                ProductFeed.class);
    }

    /** Also needs the {@code publish} scope. */
    public void deleteProductFeed(String catalogId, String feedId, String workspaceId, String connectionId) {
        http.request(
                "DELETE",
                "/v1/ads/catalogs/" + catalogId + "/feeds/" + feedId,
                null,
                connectionQuery(workspaceId, connectionId));
    }

    /** Each run the network made of the feed. */
    public List<ProductFeedUpload> feedUploads(String catalogId, String feedId, String connectionId) {
        return feedUploads(catalogId, feedId, connectionId, null);
    }

    public List<ProductFeedUpload> feedUploads(
            String catalogId, String feedId, String connectionId, String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get(
                        "/v1/ads/catalogs/" + catalogId + "/feeds/" + feedId + "/uploads",
                        connectionQuery(workspaceId, connectionId))),
                ProductFeedUpload.class);
    }

    /** Fetches the feed now and returns the id of the run. Also needs the {@code publish} scope. */
    public String startFeedUpload(String catalogId, String feedId, StartFeedUploadParams params) {
        return ApiClient.unwrap(http.post(
                        "/v1/ads/catalogs/" + catalogId + "/feeds/" + feedId + "/uploads", params.toMap()))
                .path("id")
                .asText();
    }

    /** A catalog ad runs from a product set, not the whole catalog. */
    public List<ProductSet> productSets(String catalogId, String connectionId) {
        return productSets(catalogId, connectionId, null);
    }

    public List<ProductSet> productSets(String catalogId, String connectionId, String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get(
                        "/v1/ads/catalogs/" + catalogId + "/product-sets",
                        connectionQuery(workspaceId, connectionId))),
                ProductSet.class);
    }

    /** Also needs the {@code publish} scope. */
    public ProductSet createProductSet(String catalogId, ProductSetParams params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/ads/catalogs/" + catalogId + "/product-sets", params.toMap())),
                ProductSet.class);
    }

    /** Also needs the {@code publish} scope. */
    public ProductSet updateProductSet(
            String catalogId, String setId, String workspaceId, String connectionId, ProductSetParams params) {
        return http.convert(
                ApiClient.unwrap(http.request(
                        "PATCH",
                        "/v1/ads/catalogs/" + catalogId + "/product-sets/" + setId,
                        params.toMap(),
                        connectionQuery(workspaceId, connectionId))),
                ProductSet.class);
    }

    /** Also needs the {@code publish} scope. */
    public void deleteProductSet(String catalogId, String setId, String workspaceId, String connectionId) {
        http.request(
                "DELETE",
                "/v1/ads/catalogs/" + catalogId + "/product-sets/" + setId,
                null,
                connectionQuery(workspaceId, connectionId));
    }

    // ─── Reach and frequency ────────────────────────────────────────

    public List<ReachFrequencyPrediction> reachFrequency(String connectionId, String adAccountId) {
        return reachFrequency(connectionId, adAccountId, null);
    }

    public List<ReachFrequencyPrediction> reachFrequency(
            String connectionId, String adAccountId, String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get(
                                "/v1/ads/reach-frequency", accountQuery(workspaceId, connectionId, adAccountId)))
                        .path("predictions"),
                ReachFrequencyPrediction.class);
    }

    /** Prices a flight. Nothing is bought until you reserve it. */
    public ReachFrequencyPrediction createReachFrequency(CreateReachFrequencyParams params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/ads/reach-frequency", params.toMap())),
                ReachFrequencyPrediction.class);
    }

    public ReachFrequencyPrediction reachFrequencyPrediction(
            String predictionId, String connectionId, String adAccountId) {
        return reachFrequencyPrediction(predictionId, connectionId, adAccountId, null);
    }

    public ReachFrequencyPrediction reachFrequencyPrediction(
            String predictionId, String connectionId, String adAccountId, String workspaceId) {
        return http.convert(
                ApiClient.unwrap(http.get(
                        "/v1/ads/reach-frequency/" + predictionId,
                        accountQuery(workspaceId, connectionId, adAccountId))),
                ReachFrequencyPrediction.class);
    }

    /** Holds the inventory the prediction priced. Also needs the {@code publish} scope. */
    public ReachFrequencyPrediction reserveReachFrequency(
            String predictionId, ReachFrequencyActionParams params) {
        return reachFrequencyAction(predictionId, "reserve", params);
    }

    /** Also needs the {@code publish} scope. */
    public ReachFrequencyPrediction cancelReachFrequency(
            String predictionId, ReachFrequencyActionParams params) {
        return reachFrequencyAction(predictionId, "cancel", params);
    }

    // ─── Ad Library ─────────────────────────────────────────────────

    /**
     * The public ad archive: ads anyone is running, by keyword or by Page. Read live on every call
     * and stored nowhere, so an ad that stops running is simply absent from the next search.
     * {@code countries} are two-letter codes the ad reached.
     */
    public AdLibraryPage library(String connectionId, List<String> countries, String query) {
        return library(connectionId, countries, query, null, null, null);
    }

    public AdLibraryPage library(
            String connectionId,
            List<String> countries,
            String query,
            List<String> pageIds,
            String activeStatus,
            String workspaceId) {
        Map<String, Object> params = connectionQuery(workspaceId, connectionId);
        params.put("countries", String.join(",", countries));
        if (query != null) {
            params.put("q", query);
        }
        if (pageIds != null && !pageIds.isEmpty()) {
            params.put("page_ids", String.join(",", pageIds));
        }
        if (activeStatus != null) {
            params.put("active_status", activeStatus);
        }
        return http.convert(ApiClient.unwrap(http.get("/v1/ads/library", params)), AdLibraryPage.class);
    }

    // ─── Partnership ads ────────────────────────────────────────────

    /** Creators who allowlisted this Page to run partnership ads on their posts. */
    public List<PartnershipCreator> partnershipCreators(String connectionId, String pageId) {
        return partnershipCreators(connectionId, pageId, null);
    }

    public List<PartnershipCreator> partnershipCreators(
            String connectionId, String pageId, String workspaceId) {
        Map<String, Object> query = connectionQuery(workspaceId, connectionId);
        query.put("page_id", pageId);
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/partnership/creators", query)), PartnershipCreator.class);
    }

    /** Asks a creator for permission and returns the list as it now stands. */
    public List<PartnershipCreator> requestPartnership(PartnershipParams params) {
        return http.convertList(
                ApiClient.unwrap(http.post("/v1/ads/partnership/creators", params.toMap())),
                PartnershipCreator.class);
    }

    public void revokePartnership(String creatorId, String workspaceId, String connectionId, String pageId) {
        Map<String, Object> query = connectionQuery(workspaceId, connectionId);
        query.put("page_id", pageId);
        http.request("DELETE", "/v1/ads/partnership/creators/" + creatorId, null, query);
    }

    // ─── Ad account settings ────────────────────────────────────────

    /** Who changed what on the ad account, and when. Dates are YYYY-MM-DD. */
    public List<AdActivity> accountActivity(String connectionId, String adAccountId) {
        return accountActivity(connectionId, adAccountId, null, null, null);
    }

    public List<AdActivity> accountActivity(
            String connectionId, String adAccountId, String since, String until, String workspaceId) {
        Map<String, Object> query = accountQuery(workspaceId, connectionId, adAccountId);
        if (since != null) {
            query.put("since", since);
        }
        if (until != null) {
            query.put("until", until);
        }
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/ads/account/activity", query)).path("activity"), AdActivity.class);
    }

    public List<AdLabel> labels(String connectionId, String adAccountId) {
        return labels(connectionId, adAccountId, null);
    }

    public List<AdLabel> labels(String connectionId, String adAccountId, String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get(
                        "/v1/ads/account/labels", accountQuery(workspaceId, connectionId, adAccountId))),
                AdLabel.class);
    }

    public AdLabel createLabel(AdLabelParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/ads/account/labels", params.toMap())), AdLabel.class);
    }

    public AdLabel updateLabel(String labelId, String workspaceId, String connectionId, AdLabelParams params) {
        return http.convert(
                ApiClient.unwrap(http.request(
                        "PATCH",
                        "/v1/ads/account/labels/" + labelId,
                        params.toMap(),
                        connectionQuery(workspaceId, connectionId))),
                AdLabel.class);
    }

    public void deleteLabel(String labelId, String workspaceId, String connectionId, String adAccountId) {
        http.request(
                "DELETE",
                "/v1/ads/account/labels/" + labelId,
                null,
                accountQuery(workspaceId, connectionId, adAccountId));
    }

    /** Keeps whatever labels the object already carries. */
    public void applyLabel(String labelId, ApplyAdLabelParams params) {
        http.post("/v1/ads/account/labels/" + labelId + "/apply", params.toMap());
    }

    public List<AdStudy> studies(String connectionId, String adAccountId) {
        return studies(connectionId, adAccountId, null);
    }

    public List<AdStudy> studies(String connectionId, String adAccountId, String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get(
                        "/v1/ads/account/studies", accountQuery(workspaceId, connectionId, adAccountId))),
                AdStudy.class);
    }

    /** Splits traffic evenly across the cells for the length of the flight. */
    public AdStudy createStudy(CreateAdStudyParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/ads/account/studies", params.toMap())), AdStudy.class);
    }

    public AdStudy study(String studyId, String connectionId, String adAccountId) {
        return study(studyId, connectionId, adAccountId, null);
    }

    public AdStudy study(String studyId, String connectionId, String adAccountId, String workspaceId) {
        return http.convert(
                ApiClient.unwrap(http.get(
                        "/v1/ads/account/studies/" + studyId,
                        accountQuery(workspaceId, connectionId, adAccountId))),
                AdStudy.class);
    }

    public void deleteStudy(String studyId, String workspaceId, String connectionId, String adAccountId) {
        http.request(
                "DELETE",
                "/v1/ads/account/studies/" + studyId,
                null,
                accountQuery(workspaceId, connectionId, adAccountId));
    }

    /** How many iOS 14 campaigns the account may run at once, per app. */
    public List<IosCampaignLimits> iosCampaignLimits(String connectionId, String adAccountId) {
        return iosCampaignLimits(connectionId, adAccountId, null);
    }

    public List<IosCampaignLimits> iosCampaignLimits(
            String connectionId, String adAccountId, String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get(
                        "/v1/ads/account/ios-limits", accountQuery(workspaceId, connectionId, adAccountId))),
                IosCampaignLimits.class);
    }

    public List<HighDemandPeriod> highDemandPeriods(String connectionId, String adAccountId) {
        return highDemandPeriods(connectionId, adAccountId, null);
    }

    public List<HighDemandPeriod> highDemandPeriods(
            String connectionId, String adAccountId, String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get(
                        "/v1/ads/account/high-demand-periods",
                        accountQuery(workspaceId, connectionId, adAccountId))),
                HighDemandPeriod.class);
    }

    /** Tells the network to expect heavier spend over a window, so pacing allows for it. */
    public HighDemandPeriod createHighDemandPeriod(CreateHighDemandPeriodParams params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/ads/account/high-demand-periods", params.toMap())),
                HighDemandPeriod.class);
    }

    public void deleteHighDemandPeriod(
            String periodId, String workspaceId, String connectionId, String adAccountId) {
        http.request(
                "DELETE",
                "/v1/ads/account/high-demand-periods/" + periodId,
                null,
                accountQuery(workspaceId, connectionId, adAccountId));
    }

    public List<ValueRuleSet> valueRuleSets(String connectionId, String adAccountId) {
        return valueRuleSets(connectionId, adAccountId, null);
    }

    public List<ValueRuleSet> valueRuleSets(String connectionId, String adAccountId, String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get(
                        "/v1/ads/account/value-rule-sets",
                        accountQuery(workspaceId, connectionId, adAccountId))),
                ValueRuleSet.class);
    }

    /** Weights conversions so some audiences count for more than others. */
    public ValueRuleSet createValueRuleSet(CreateValueRuleSetParams params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/ads/account/value-rule-sets", params.toMap())),
                ValueRuleSet.class);
    }

    public void deleteValueRuleSet(
            String ruleSetId, String workspaceId, String connectionId, String adAccountId) {
        http.request(
                "DELETE",
                "/v1/ads/account/value-rule-sets/" + ruleSetId,
                null,
                accountQuery(workspaceId, connectionId, adAccountId));
    }

    private ReachFrequencyPrediction reachFrequencyAction(
            String predictionId, String action, ReachFrequencyActionParams params) {
        return http.convert(
                ApiClient.unwrap(
                        http.post("/v1/ads/reach-frequency/" + predictionId + "/" + action, params.toMap())),
                ReachFrequencyPrediction.class);
    }

    private static Map<String, Object> accountQuery(
            String workspaceId, String connectionId, String adAccountId) {
        Map<String, Object> query = connectionQuery(workspaceId, connectionId);
        query.put("ad_account_id", adAccountId);
        return query;
    }

    private static Map<String, Object> connectionQuery(String workspaceId, String connectionId) {
        Map<String, Object> query = workspaceQuery(workspaceId);
        query.put("connection_id", connectionId);
        return query;
    }

    private static Map<String, Object> workspaceQuery(String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (workspaceId != null) {
            query.put("workspace_id", workspaceId);
        }
        return query;
    }
}
