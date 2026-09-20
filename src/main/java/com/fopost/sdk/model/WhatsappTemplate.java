package com.fopost.sdk.model;

import java.util.List;
import java.util.Map;

/**
 * A message template. {@code status} is the review outcome the platform
 * assigned, passed through unchanged: nothing marks a template approved but the
 * platform.
 */
public record WhatsappTemplate(
        String id,
        String name,
        String language,
        String category,
        String status,
        String rejectedReason,
        List<Map<String, Object>> components,
        String qualityScore) {}
