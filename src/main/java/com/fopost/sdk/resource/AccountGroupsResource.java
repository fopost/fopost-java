package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.AccountGroup;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Account groups: named sets of accounts a post can target with {@code accountGroupId}.
 *
 * <pre>{@code
 * AccountGroup group = client.accountGroups().create(workspaceId, "Brand A", List.of(accountId));
 * client.posts().create(CreatePostParams.of(workspaceId).accountGroupId(group.id()).content("Hi"));
 * }</pre>
 */
public final class AccountGroupsResource {

    private final ApiClient http;

    public AccountGroupsResource(ApiClient http) {
        this.http = http;
    }

    public List<AccountGroup> list() {
        return list(null);
    }

    public List<AccountGroup> list(String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (workspaceId != null) {
            query.put("workspace_id", workspaceId);
        }
        return http.convertList(ApiClient.unwrap(http.get("/v1/account-groups", query)), AccountGroup.class);
    }

    public AccountGroup get(String groupId) {
        return http.convert(ApiClient.unwrap(http.get("/v1/account-groups/" + groupId, null)), AccountGroup.class);
    }

    public AccountGroup create(String workspaceId, String name) {
        return create(workspaceId, name, null);
    }

    public AccountGroup create(String workspaceId, String name, List<String> accountIds) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspace_id", workspaceId);
        body.put("name", name);
        if (accountIds != null) {
            body.put("account_ids", accountIds);
        }
        return http.convert(ApiClient.unwrap(http.post("/v1/account-groups", body)), AccountGroup.class);
    }

    /** Rename the group. */
    public AccountGroup update(String groupId, String name) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", "/v1/account-groups/" + groupId, body, null)),
                AccountGroup.class);
    }

    /** Deletes the group only; its accounts stay connected. */
    public void delete(String groupId) {
        http.delete("/v1/account-groups/" + groupId);
    }

    /** Replace the group's members with exactly {@code accountIds}. */
    public AccountGroup setMembers(String groupId, List<String> accountIds) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("account_ids", accountIds);
        return http.convert(
                ApiClient.unwrap(http.put("/v1/account-groups/" + groupId + "/members", body)), AccountGroup.class);
    }
}
