package com.fopost.sdk.param;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Filters shared by the analytics endpoints. Every field is optional, and each endpoint reads
 * the ones that apply to it.
 *
 * <p>Give either {@code days} or a {@code from}/{@code to} pair; a range wins where both are sent.
 */
public final class AnalyticsParams {

    private String accountId;
    private String workspaceId;
    private Integer days;
    private String from;
    private String to;
    private Integer limit;
    private Integer page;
    private String sort;
    private String label;
    private String audience;

    public static AnalyticsParams create() {
        return new AnalyticsParams();
    }

    /** Narrow to one connected account. */
    public AnalyticsParams accountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public AnalyticsParams workspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
        return this;
    }

    /** How many days back to look. */
    public AnalyticsParams days(int days) {
        this.days = days;
        return this;
    }

    public AnalyticsParams from(LocalDate from) {
        this.from = from == null ? null : from.toString();
        return this;
    }

    public AnalyticsParams to(LocalDate to) {
        this.to = to == null ? null : to.toString();
        return this;
    }

    public AnalyticsParams limit(int limit) {
        this.limit = limit;
        return this;
    }

    public AnalyticsParams page(int page) {
        this.page = page;
        return this;
    }

    /** Top posts only: which metric to rank on. */
    public AnalyticsParams sort(String sort) {
        this.sort = sort;
        return this;
    }

    public AnalyticsParams label(String labelId) {
        this.label = labelId;
        return this;
    }

    /** Demographics only: {@code followers}, {@code engaged} or {@code reached}. */
    public AnalyticsParams audience(String audience) {
        this.audience = audience;
        return this;
    }

    public Map<String, Object> toQuery() {
        Map<String, Object> query = new LinkedHashMap<>();
        Params.put(query, "accountId", accountId);
        Params.put(query, "workspace_id", workspaceId);
        Params.put(query, "days", days);
        Params.put(query, "from", from);
        Params.put(query, "to", to);
        Params.put(query, "limit", limit);
        Params.put(query, "page", page);
        Params.put(query, "sort", sort);
        Params.put(query, "label", label);
        Params.put(query, "audience", audience);
        return query;
    }
}
