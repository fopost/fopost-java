package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.internal.Json;
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
        return list(workspaceId, null);
    }

    /** {@code groupId} keeps only the accounts in that account group. Either argument may be null. */
    public List<Account> list(String workspaceId, String groupId) {
        Map<String, Object> params = new LinkedHashMap<>();
        if (workspaceId != null) {
            params.put("workspaceId", workspaceId);
        }
        if (groupId != null) {
            params.put("group_id", groupId);
        }
        return http.convertList(ApiClient.unwrap(http.get("/v1/accounts", params.isEmpty() ? null : params)),
                Account.class);
    }

    public Account get(String accountId) {
        return http.convert(ApiClient.unwrap(http.get("/v1/accounts/" + accountId, null)), Account.class);
    }

    /** Connect an account with credentials you already hold, instead of the dashboard OAuth flow. */
    public Account create(CreateAccountParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/accounts", params.toMap())), Account.class);
    }

    /**
     * Set the name shown instead of the platform name. {@code null} or an empty string restores the
     * platform name. Returns {@code id}, {@code name} and {@code platformName}.
     */
    public Account rename(String accountId, String displayName) {
        // An ObjectNode keeps an explicit null, which the shared mapper would drop from a Map.
        ObjectNode body = Json.MAPPER.createObjectNode().put("display_name", displayName);
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", "/v1/accounts/" + accountId, body, null)), Account.class);
    }

    /**
     * Move the account to another workspace the caller owns. Returns {@code id} and {@code workspaceId}.
     *
     * <p>A 409 is a {@code FoPostException}: code {@code move_blocked} carries {@code blocking_tables} on
     * {@code body()}; otherwise the target already has an account on that network.
     */
    public Account move(String accountId, String workspaceId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspace_id", workspaceId);
        return http.convert(
                ApiClient.unwrap(http.post("/v1/accounts/" + accountId + "/move", body)), Account.class);
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
