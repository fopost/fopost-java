package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Community;
import com.fopost.sdk.model.CommunitySearchResult;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The X communities an account can post into. */
public final class CommunitiesResource {

    private final ApiClient http;

    public CommunitiesResource(ApiClient http) {
        this.http = http;
    }

    public List<Community> list(String accountId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/communities", null)), Community.class);
    }

    /** Re-read the account's communities from X and store what came back. */
    public List<Community> sync(String accountId) {
        return http.convertList(
                ApiClient.unwrap(http.post("/v1/accounts/" + accountId + "/communities/sync", null)), Community.class);
    }

    /** Search X directly, for a community the account has not joined. */
    public List<CommunitySearchResult> search(String accountId, String query) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("q", query);
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/communities/search", params)),
                CommunitySearchResult.class);
    }

    /** Add a community by its id on X, for one search cannot reach. */
    public Community add(String accountId, String communityId, String name) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("communityId", communityId);
        if (name != null) {
            body.put("name", name);
        }
        return http.convert(
                ApiClient.unwrap(http.post("/v1/accounts/" + accountId + "/communities/manual", body)),
                Community.class);
    }

    public void remove(String accountId, long communityId) {
        http.delete("/v1/accounts/" + accountId + "/communities/" + communityId);
    }
}
