package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * An automation: one trigger and the ordered steps it runs.
 *
 * <p>{@code steps} and {@code secret} arrive from the detail and create calls; the list gives
 * the summary fields only.
 */
public record Automation(
        String id,
        String workspaceId,
        String name,
        String triggerType,
        Map<String, Object> triggerConfig,
        Boolean active,
        String secret,
        List<Step> steps,
        Instant lastTriggeredAt,
        Integer runCount,
        Instant createdAt,
        Instant updatedAt) {

    /** One step of an automation. {@code actionType} is publish, delay or transform. */
    public record Step(Integer id, Integer position, String actionType, Map<String, Object> actionConfig) {}
}
