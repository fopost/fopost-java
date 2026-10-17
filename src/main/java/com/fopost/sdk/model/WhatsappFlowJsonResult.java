package com.fopost.sdk.model;

import java.util.List;

/**
 * The platform's verdict on an uploaded flow definition. It answers with the
 * errors rather than refusing the upload, so they arrive as data.
 */
public record WhatsappFlowJsonResult(boolean success, List<WhatsappFlowValidationError> validationErrors) {}
