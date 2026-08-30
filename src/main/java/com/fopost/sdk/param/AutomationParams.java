package com.fopost.sdk.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An automation being created or updated.
 *
 * <pre>{@code
 * AutomationParams.create(workspaceId, "Cross-post to LinkedIn", "cross_post")
 *     .triggerConfig(Map.of("sourceAccountId", accountId))
 *     .step("publish", Map.of("accountIds", List.of(targetId)));
 * }</pre>
 */
public final class AutomationParams {

    private final Map<String, Object> body = new LinkedHashMap<>();
    private final List<Map<String, Object>> steps = new ArrayList<>();
    private boolean stepsSet;

    /** {@code triggerType} is cross_post, rss_feed, api_webhook or schedule. */
    public static AutomationParams create(String workspaceId, String name, String triggerType) {
        AutomationParams params = new AutomationParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("name", name);
        params.body.put("triggerType", triggerType);
        return params;
    }

    public static AutomationParams update() {
        return new AutomationParams();
    }

    public AutomationParams name(String name) {
        body.put("name", name);
        return this;
    }

    public AutomationParams triggerConfig(Map<String, Object> triggerConfig) {
        body.put("triggerConfig", triggerConfig);
        return this;
    }

    /** Append a step. {@code actionType} is publish, delay or transform. */
    public AutomationParams step(String actionType, Map<String, Object> actionConfig) {
        Map<String, Object> step = new LinkedHashMap<>();
        step.put("actionType", actionType);
        Params.put(step, "actionConfig", actionConfig);
        steps.add(step);
        stepsSet = true;
        return this;
    }

    public AutomationParams active(boolean active) {
        body.put("active", active);
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> out = new LinkedHashMap<>(body);
        if (stepsSet) {
            out.put("steps", steps);
        }
        return out;
    }
}
