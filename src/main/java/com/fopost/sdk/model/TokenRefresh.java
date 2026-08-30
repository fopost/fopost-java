package com.fopost.sdk.model;

import java.time.Instant;

/** The outcome of refreshing an account's OAuth token. */
public record TokenRefresh(String message, Instant expiresAt) {}
