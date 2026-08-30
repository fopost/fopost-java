package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Label;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Workspace labels, for grouping posts into campaigns. */
public final class LabelsResource {

    private final ApiClient http;

    public LabelsResource(ApiClient http) {
        this.http = http;
    }

    public List<Label> list() {
        return list(null);
    }

    public List<Label> list(String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (workspaceId != null) {
            query.put("workspace_id", workspaceId);
        }
        return http.convertList(ApiClient.unwrap(http.get("/v1/labels", query)), Label.class);
    }

    public Label get(String labelId) {
        return http.convert(ApiClient.unwrap(http.get("/v1/labels/" + labelId, null)), Label.class);
    }

    /** {@code color} is a hex value, e.g. {@code #4F46E5}. */
    public Label create(String workspaceId, String name, String color) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspace_id", workspaceId);
        body.put("name", name);
        body.put("color", color);
        return http.convert(ApiClient.unwrap(http.post("/v1/labels", body)), Label.class);
    }

    public Label update(String labelId, String name, String color) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        body.put("color", color);
        return http.convert(ApiClient.unwrap(http.put("/v1/labels/" + labelId, body)), Label.class);
    }

    /** Deletes the label and detaches it from every post that carried it. */
    public void delete(String labelId) {
        http.delete("/v1/labels/" + labelId);
    }
}
