package com.fopost.sdk.model;

import java.time.Instant;
import java.util.Map;

/** What one person submitted through a flow. */
public record WhatsappFlowResponse(
        String messageId,
        String waId,
        String flowToken,
        Map<String, Object> answers,
        Instant respondedAt) {}
