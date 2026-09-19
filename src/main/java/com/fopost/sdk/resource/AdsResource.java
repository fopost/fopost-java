package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Ad;
import com.fopost.sdk.model.AdConnection;
import com.fopost.sdk.model.AdSource;
import com.fopost.sdk.model.AudiencesResult;
import com.fopost.sdk.model.BoostablePost;
import com.fopost.sdk.model.CreatedAudience;
import com.fopost.sdk.model.ExternalAd;
import com.fopost.sdk.model.LeadFormSource;
import com.fopost.sdk.model.LeadsPage;
import com.fopost.sdk.model.TargetingOption;
import com.fopost.sdk.param.BoostPostParams;
import com.fopost.sdk.param.CreateAdParams;
import com.fopost.sdk.param.CreateAudienceParams;
import com.fopost.sdk.param.CreateLeadFormParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Boosts, ads, audiences and lead forms on the connected ad accounts.
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
 * {@link #setStatus} and {@link #delete} spend money and also need the {@code publish} scope. A
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

    // ─── Plumbing ─────────────────────────────────────────────────────────────

    private static Map<String, Object> workspaceQuery(String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (workspaceId != null) {
            query.put("workspace_id", workspaceId);
        }
        return query;
    }
}
