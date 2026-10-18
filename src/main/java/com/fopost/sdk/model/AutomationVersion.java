package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * One saved version of an automation's graph. The list carries {@code nodeCount},
 * {@code nodesAdded} and {@code nodesRemoved}; the single-version call carries {@code graph}.
 */
public record AutomationVersion(
        Integer version,
        String note,
        Author createdBy,
        Instant createdAt,
        Integer nodeCount,
        List<NodeRef> nodesAdded,
        List<NodeRef> nodesRemoved,
        AutomationGraph graph) {

    public record Author(String name) {}

    public record NodeRef(String id, String kind, String label) {}
}
