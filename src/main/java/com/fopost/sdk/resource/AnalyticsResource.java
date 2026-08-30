package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.AnalyticsOverview;
import com.fopost.sdk.model.CollectSummary;
import com.fopost.sdk.model.Demographics;
import com.fopost.sdk.model.LabelAnalytics;
import com.fopost.sdk.model.PostingStreak;
import com.fopost.sdk.model.PostsTable;
import com.fopost.sdk.model.TimeSeries;
import com.fopost.sdk.model.TopPost;
import com.fopost.sdk.param.AnalyticsParams;
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
}
