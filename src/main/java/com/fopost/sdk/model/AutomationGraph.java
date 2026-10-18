package com.fopost.sdk.model;

import java.util.List;
import java.util.Map;

/** The nodes and edges of a saved automation graph. */
public record AutomationGraph(List<Node> nodes, List<Edge> edges) {

    /** {@code config} is echoed back exactly as authored. */
    public record Node(String id, String kind, String label, Map<String, Object> config, Integer position, Layout layout) {}

    public record Layout(Double x, Double y) {}

    public record Edge(String id, String source, String sourceHandle, String target) {}
}
