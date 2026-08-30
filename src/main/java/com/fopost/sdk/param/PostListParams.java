package com.fopost.sdk.param;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/** Filters for listing posts. Every field is optional. */
public final class PostListParams {

    private String workspaceId;
    private String status;
    private String search;
    private String platform;
    private String label;
    private String accountId;
    private String date;
    private String from;
    private String to;
    private String sort;
    private Integer page;
    private Integer perPage;

    public static PostListParams create() {
        return new PostListParams();
    }

    public PostListParams workspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
        return this;
    }

    /** One of the values in {@link com.fopost.sdk.model.PostStatus}. */
    public PostListParams status(String status) {
        this.status = status;
        return this;
    }

    public PostListParams search(String search) {
        this.search = search;
        return this;
    }

    public PostListParams platform(String platform) {
        this.platform = platform;
        return this;
    }

    public PostListParams label(String labelId) {
        this.label = labelId;
        return this;
    }

    public PostListParams accountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    /** A single day, as {@code YYYY-MM-DD}. */
    public PostListParams date(LocalDate date) {
        this.date = date == null ? null : date.toString();
        return this;
    }

    public PostListParams from(LocalDate from) {
        this.from = from == null ? null : from.toString();
        return this;
    }

    public PostListParams to(LocalDate to) {
        this.to = to == null ? null : to.toString();
        return this;
    }

    public PostListParams sort(String sort) {
        this.sort = sort;
        return this;
    }

    public PostListParams page(int page) {
        this.page = page;
        return this;
    }

    public PostListParams perPage(int perPage) {
        this.perPage = perPage;
        return this;
    }

    public Integer page() {
        return page;
    }

    public Integer perPage() {
        return perPage;
    }

    /** A copy of these filters pinned to one page — used by the auto-paginating helpers. */
    public PostListParams withPage(int page) {
        PostListParams copy = new PostListParams();
        copy.workspaceId = workspaceId;
        copy.status = status;
        copy.search = search;
        copy.platform = platform;
        copy.label = label;
        copy.accountId = accountId;
        copy.date = date;
        copy.from = from;
        copy.to = to;
        copy.sort = sort;
        copy.perPage = perPage;
        copy.page = page;
        return copy;
    }

    public Map<String, Object> toQuery() {
        Map<String, Object> query = new LinkedHashMap<>();
        Params.put(query, "workspace_id", workspaceId);
        Params.put(query, "status", status);
        Params.put(query, "search", search);
        Params.put(query, "platform", platform);
        Params.put(query, "label", label);
        Params.put(query, "account_id", accountId);
        Params.put(query, "date", date);
        Params.put(query, "from", from);
        Params.put(query, "to", to);
        Params.put(query, "sort", sort);
        Params.put(query, "page", page);
        Params.put(query, "per_page", perPage);
        return query;
    }
}
