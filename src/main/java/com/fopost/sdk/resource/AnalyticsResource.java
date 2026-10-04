package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.AnalyticsOverview;
import com.fopost.sdk.model.CollectPostResult;
import com.fopost.sdk.model.CollectSummary;
import com.fopost.sdk.model.ContentDecay;
import com.fopost.sdk.model.Demographics;
import com.fopost.sdk.model.LabelAnalytics;
import com.fopost.sdk.model.MetricChangePage;
import com.fopost.sdk.model.NativePost;
import com.fopost.sdk.model.Page;
import com.fopost.sdk.model.PageMeta;
import com.fopost.sdk.model.PostTimeline;
import com.fopost.sdk.model.PostingFrequency;
import com.fopost.sdk.model.PostingStreak;
import com.fopost.sdk.model.PostsTable;
import com.fopost.sdk.model.TimeSeries;
import com.fopost.sdk.model.TopPost;
import com.fopost.sdk.param.AnalyticsParams;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reach and engagement across the accounts a key can see. */
public final class AnalyticsResource {

    private final ApiClient http;

    public AnalyticsResource(ApiClient http) {
        this.http = http;
    }

    public AnalyticsOverview overview() {
        return overview(AnalyticsParams.create());
    }

    /** Headline totals, deltas against the previous window, and the per-platform split. */
    public AnalyticsOverview overview(AnalyticsParams params) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/analytics/overview", params.toQuery())), AnalyticsOverview.class);
    }

    /** One point per day over the window. */
    public TimeSeries timeSeries(AnalyticsParams params) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/analytics/time-series", params.toQuery())), TimeSeries.class);
    }

    public List<TopPost> topPosts(AnalyticsParams params) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/analytics/top-posts", params.toQuery())), TopPost.class);
    }

    /** Campaign roll-up, one row per label. */
    public List<LabelAnalytics> labels(AnalyticsParams params) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/analytics/labels", params.toQuery())), LabelAnalytics.class);
    }

    public PostsTable postsTable(AnalyticsParams params) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/analytics/posts-table", params.toQuery())), PostsTable.class);
    }

    /** A year of posting activity, a row per day. */
    public PostingStreak postingStreak(AnalyticsParams params) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/analytics/posting-streak", params.toQuery())), PostingStreak.class);
    }

    /** Audience demographics, aggregated over the accounts whose platform reports them. */
    public Demographics demographics(AnalyticsParams params) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/analytics/demographics", params.toQuery())), Demographics.class);
    }

    /** Fetch fresh figures from the platforms now, rather than waiting for the next scheduled pass. */
    public CollectSummary collect() {
        return collect(null);
    }

    public CollectSummary collect(String accountId) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (accountId != null) {
            query.put("accountId", accountId);
        }
        return http.convert(ApiClient.unwrap(http.post("/v1/analytics/collect", null, query)), CollectSummary.class);
    }

    /**
     * How long a post keeps earning: engagement grouped by the post's age at each reading.
     *
     * <p>{@code days} selects posts by publish time, not reading time.
     */
    public ContentDecay decay(AnalyticsParams params) {
        return http.convert(ApiClient.unwrap(http.get("/v1/analytics/decay", params.toQuery())), ContentDecay.class);
    }

    public ContentDecay decay() {
        return decay(AnalyticsParams.create());
    }

    /** Whether posting more earned more: weekly cadence against what each cadence earned per post. */
    public PostingFrequency frequency(AnalyticsParams params) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/analytics/frequency", params.toQuery())), PostingFrequency.class);
    }

    public PostingFrequency frequency() {
        return frequency(AnalyticsParams.create());
    }

    /**
     * Every reading held for one post, oldest first, with what moved between them.
     *
     * @param idOrPermalink a FoPost post id, or the permalink of a post made natively on the network
     */
    public PostTimeline timeline(String idOrPermalink) {
        String path = "/v1/analytics/posts/" + encodeSegment(idOrPermalink) + "/timeline";
        return http.convert(ApiClient.unwrap(http.get(path, null)), PostTimeline.class);
    }

    /**
     * Readings recorded after {@code since}, oldest first, with a cursor to continue.
     *
     * <p>Poll it to mirror the metrics into your own store instead of refetching the whole history.
     * Without a {@code since} it answers with the last seven days.
     */
    public MetricChangePage changes(AnalyticsParams params) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/analytics/changes", params.toQuery())), MetricChangePage.class);
    }

    public MetricChangePage changes() {
        return changes(AnalyticsParams.create());
    }

    /**
     * Re-read one post from the network now. Spends the same per-user budget as {@link #collect()},
     * so a burst answers 429.
     *
     * @param idOrPermalink a FoPost post id, or the permalink of a post made natively on the network
     */
    public CollectPostResult collectPost(String idOrPermalink) {
        String path = "/v1/posts/" + encodeSegment(idOrPermalink) + "/analytics/collect";
        return http.convert(ApiClient.unwrap(http.post(path, null, null)), CollectPostResult.class);
    }

    /** Posts on the account that never went out through FoPost, newest first. */
    public Page<NativePost> nativePosts(String accountId, AnalyticsParams params) {
        JsonNode body = http.get("/v1/accounts/" + accountId + "/native-posts", params.toQuery());
        return new Page<>(
                http.convertList(body.path("data"), NativePost.class), http.convert(body.path("meta"), PageMeta.class));
    }

    public Page<NativePost> nativePosts(String accountId) {
        return nativePosts(accountId, AnalyticsParams.create());
    }

    /** A post can be addressed by permalink, whose slashes would otherwise split the path. */
    private static String encodeSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
