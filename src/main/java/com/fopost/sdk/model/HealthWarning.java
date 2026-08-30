package com.fopost.sdk.model;

/** An account whose credentials look shaky, reported alongside a publish. */
public record HealthWarning(String accountId, String platform, String healthStatus, String message) {}
