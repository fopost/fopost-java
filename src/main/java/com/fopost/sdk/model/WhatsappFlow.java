package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** An in-chat form. The platform validates it and owns its status. */
public record WhatsappFlow(
        String id,
        String name,
        String status,
        List<String> categories,
        List<WhatsappFlowValidationError> validationErrors,
        String endpointUri,
        String jsonVersion,
        String previewUrl,
        Instant previewExpiresAt) {}
