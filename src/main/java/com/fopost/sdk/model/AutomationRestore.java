package com.fopost.sdk.model;

import java.util.List;
import java.util.Map;

/** The answer to a restore: the new version number, its graph, and the steps compiled from it. */
public record AutomationRestore(Integer version, AutomationGraph graph, List<Step> steps) {

    public record Step(Integer position, String nodeId, String actionType, Map<String, Object> actionConfig) {}
}
