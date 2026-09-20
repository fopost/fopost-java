package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Ad;
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
 * Boosts, ads, campaigns, creatives, audiences, insights and lead forms on the connected ad accounts.
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

    /** Connects Meta, the default network. */
    public String authorize(String workspaceId) {
        return authorize(workspaceId, "meta", null, null);
    }

    /**
     * The login url for connecting an ad account. The user who calls this must finish the login in
     * their own browser session. {@code provider} names the ad network and defaults to meta;
     * {@code method} is the network's own login method, business or user on Meta; {@code returnTo}
     * is the dashboard path to land on afterwards. A network that is not available on the
     * deployment answers 503.
     */
    public String authorize(String workspaceId, String provider, String method, String returnTo) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspaceId", workspaceId);
        if (method != null) {
            body.put("method", method);
        }
        if (returnTo != null) {
            body.put("returnTo", returnTo);
        }
        String network = provider == null || provider.isEmpty() ? "meta" : provider;
        return ApiClient.unwrap(http.post("/v1/ads/connections/" + network + "/authorize", body))
                .path("url")
                .asText();
    }

    /** @deprecated use {@link #authorize(String)}, which takes a provider. */
    @Deprecated
    public String authorizeMeta(String workspaceId) {
        return authorize(workspaceId);
    }

    /** @deprecated use {@link #authorize(String, String, String, String)}. */
    @Deprecated
    public String authorizeMeta(String workspaceId, String method, String returnTo) {
        return authorize(workspaceId, "meta", method, returnTo);
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
