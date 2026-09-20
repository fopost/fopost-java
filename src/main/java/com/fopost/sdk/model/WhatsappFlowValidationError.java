package com.fopost.sdk.model;

/** One problem the platform found in a flow definition. */
public record WhatsappFlowValidationError(String error, String message) {}
