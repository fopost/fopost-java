package com.fopost.sdk.model;

import java.time.Instant;
import java.util.Map;

/**
 * A saved version of a design.
 *
 * @param document set only when the versions were listed with their documents
 */
public record DesignVersion(String id, Integer version, Map<String, Object> document, Instant createdAt) {}
