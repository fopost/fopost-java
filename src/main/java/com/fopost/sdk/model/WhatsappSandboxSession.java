package com.fopost.sdk.model;

import java.time.Instant;

/**
 * A sandbox invitation. Only the last four digits of the tester's number
 * travel; the number itself is never stored.
 */
public record WhatsappSandboxSession(
        String id,
        String status,
        String phoneNumberLast4,
        Instant invitedAt,
        Instant activatedAt,
        Instant expiresAt) {}
