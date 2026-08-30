package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Account;
import com.fopost.sdk.model.AccountAnalyticsHistory;
import com.fopost.sdk.model.AccountHealth;
import com.fopost.sdk.model.AccountValidation;
import com.fopost.sdk.model.AccountsHealthSummary;
import com.fopost.sdk.model.TokenRefresh;
import com.fopost.sdk.param.CreateAccountParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The social accounts connected to a workspace. */
public final class AccountsResource {

    private final ApiClient http;
    private final CommunitiesResource communities;

    public AccountsResource(ApiClient http) {
        this.http = http;
        this.communities = new CommunitiesResource(http);
    }

    /** X communities, per account. */
    public CommunitiesResource communities() {
        return communities;
    }

    /** Connected accounts across every workspace the key can reach. */
    public List<Account> list() {
        return list(null);
    }

    public List<Account> list(String workspaceId) {
        return http.convertList(ApiClient.unwrap(http.get("/v1/accounts", query("workspaceId", workspaceId))),
                Account.class);
    }

    public Account get(String accountId) {
        return http.convert(ApiClient.unwrap(http.get("/v1/accounts/" + accountId, null)), Account.class);
    }

    /** Connect an account with credentials you already hold, instead of the dashboard OAuth flow. */
    public Account create(CreateAccountParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/accounts", params.toMap())), Account.class);
    }

    /** Disconnect the account. Posts already published stay where they are. */
    public void delete(String accountId) {
        http.delete("/v1/accounts/" + accountId);
    }

    /** Token validity for every account, with the counts rolled up. */
    public AccountsHealthSummary healthSummary() {
        return healthSummary(null);
    }

    public AccountsHealthSummary healthSummary(String workspaceId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/health", query("workspaceId", workspaceId))),
                AccountsHealthSummary.class);
    }

    /** Health for one account, as last recorded. */
    public AccountHealth health(String accountId) {
        return health(accountId, false);
    }

    /** {@code refresh} re-checks the credentials against the platform instead of reading the cache. */
    public AccountHealth health(String accountId, boolean refresh) {
        Map<String, Object> params = refresh ? query("refresh", "true") : null;
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/health", params)), AccountHealth.class);
    }

    /** Make this the account a post targets by default, or clear the flag. Returns the new state. */
    public boolean togglePrimary(String accountId) {
        JsonNode data = ApiClient.unwrap(http.post("/v1/accounts/" + accountId + "/primary", null));
        return data.path("isPrimary").asBoolean(false);
    }

    /** Check the stored credentials against the platform, now. */
    public AccountValidation validate(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/accounts/" + accountId + "/validate", null)), AccountValidation.class);
    }

    /** Force an OAuth token refresh, ahead of the automatic one. */
    public TokenRefresh refreshToken(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/accounts/" + accountId + "/refresh-token", null)), TokenRefresh.class);
    }

    /** Follower and reach history for the account, newest first. */
    public AccountAnalyticsHistory analytics(String accountId) {
        return analytics(accountId, null);
    }

    public AccountAnalyticsHistory analytics(String accountId, Integer limit) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/analytics", query("limit", limit))),
                AccountAnalyticsHistory.class);
    }

    private static Map<String, Object> query(String key, Object value) {
        if (value == null) {
            return null;
        }
        Map<String, Object> query = new LinkedHashMap<>();
        query.put(key, value);
        return query;
    }
}
