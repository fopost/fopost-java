package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Automation;
import com.fopost.sdk.model.AutomationRestore;
import com.fopost.sdk.model.AutomationRun;
import com.fopost.sdk.model.AutomationStats;
import com.fopost.sdk.model.AutomationTrigger;
import com.fopost.sdk.model.AutomationVersion;
import com.fopost.sdk.model.AutomationVersionPage;
import com.fopost.sdk.model.Page;
import com.fopost.sdk.model.PageMeta;
import com.fopost.sdk.param.AutomationParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Automations: a trigger, and the steps it runs. */
public final class AutomationsResource {

    private final ApiClient http;

    public AutomationsResource(ApiClient http) {
        this.http = http;
    }

    public List<Automation> list() {
        return http.convertList(ApiClient.unwrap(http.get("/v1/automations", null)), Automation.class);
    }

    /** The full automation, including its steps. */
    public Automation get(String automationId) {
        return http.convert(ApiClient.unwrap(http.get("/v1/automations/" + automationId, null)), Automation.class);
    }

    /** The webhook signing secret is on the created object and is never shown again. */
    public Automation create(AutomationParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/automations", params.toMap())), Automation.class);
    }

    public Automation update(String automationId, AutomationParams params) {
        return http.convert(
                ApiClient.unwrap(http.put("/v1/automations/" + automationId, params.toMap())), Automation.class);
    }

    public void delete(String automationId) {
        http.delete("/v1/automations/" + automationId);
    }

    /** Flip the automation on or off. Returns its new state. */
    public boolean toggle(String automationId) {
        JsonNode data = ApiClient.unwrap(http.post("/v1/automations/" + automationId + "/toggle", null));
        return data.path("active").asBoolean(false);
    }

    public Page<AutomationRun> runs(String automationId) {
        return runs(automationId, null, null);
    }

    public Page<AutomationRun> runs(String automationId, Integer page, Integer perPage) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (page != null) {
            query.put("page", page);
        }
        if (perPage != null) {
            query.put("per_page", perPage);
        }
        JsonNode body = http.get("/v1/automations/" + automationId + "/runs", query);
        return new Page<>(
                http.convertList(body.path("data"), AutomationRun.class), http.convert(body.path("meta"), PageMeta.class));
    }

    /** One run, with the per-step log of what it did. */
    public AutomationRun run(String automationId, long runId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/automations/" + automationId + "/runs/" + runId, null)),
                AutomationRun.class);
    }

    /**
     * Fire an {@code api_webhook} automation by hand.
     *
     * <p>{@code payload} becomes the run's trigger event, and the steps read it from there.
     */
    public AutomationTrigger trigger(String automationId, Map<String, Object> payload) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/automations/" + automationId + "/trigger",
                        payload == null ? Map.of() : payload)),
                AutomationTrigger.class);
    }

    /** Every saved graph, newest first, with the nodes each save added and removed. */
    public AutomationVersionPage versions(String automationId) {
        return versions(automationId, null, null);
    }

    public AutomationVersionPage versions(String automationId, Integer page, Integer perPage) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (page != null) {
            query.put("page", page);
        }
        if (perPage != null) {
            query.put("per_page", perPage);
        }
        JsonNode body = http.get("/v1/automations/" + automationId + "/versions", query);
        return new AutomationVersionPage(
                http.convertList(body.path("data"), AutomationVersion.class),
                http.convert(body.path("meta"), AutomationVersionPage.Meta.class));
    }

    /** One version, with its full graph. */
    public AutomationVersion version(String automationId, int version) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/automations/" + automationId + "/versions/" + version, null)),
                AutomationVersion.class);
    }

    /**
     * Save that version's graph as a new version on top of the history; nothing is rewritten. The
     * old graph must still validate under the automation's current trigger and workspace.
     */
    public AutomationRestore restoreVersion(String automationId, int version) {
        return http.convert(
                ApiClient.unwrap(http.post(
                        "/v1/automations/" + automationId + "/versions/" + version + "/restore", null)),
                AutomationRestore.class);
    }

    public AutomationStats stats() {
        return http.convert(ApiClient.unwrap(http.get("/v1/automations/stats", null)), AutomationStats.class);
    }
}
