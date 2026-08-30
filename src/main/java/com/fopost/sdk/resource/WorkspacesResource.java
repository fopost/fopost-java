package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Workspace;
import com.fopost.sdk.model.WorkspaceAnalytics;
import com.fopost.sdk.param.WorkspaceParams;
import java.util.List;

/** The workspaces the key can reach. */
public final class WorkspacesResource {

    private final ApiClient http;

    public WorkspacesResource(ApiClient http) {
        this.http = http;
    }

    /** Every workspace the key can reach, each with its connected accounts. */
    public List<Workspace> list() {
        return http.convertList(ApiClient.unwrap(http.get("/v1/workspaces", null)), Workspace.class);
    }

    public Workspace get(String workspaceId) {
        return http.convert(ApiClient.unwrap(http.get("/v1/workspaces/" + workspaceId, null)), Workspace.class);
    }

    public Workspace create(WorkspaceParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/workspaces", params.toMap())), Workspace.class);
    }

    public Workspace update(String workspaceId, WorkspaceParams params) {
        return http.convert(
                ApiClient.unwrap(http.put("/v1/workspaces/" + workspaceId, params.toMap())), Workspace.class);
    }

    /** Deletes the workspace and everything in it. This cannot be undone. */
    public void delete(String workspaceId) {
        http.delete("/v1/workspaces/" + workspaceId);
    }

    /** Latest follower and post figures for every account in the workspace. */
    public WorkspaceAnalytics analytics(String workspaceId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/workspaces/" + workspaceId + "/analytics", null)),
                WorkspaceAnalytics.class);
    }
}
