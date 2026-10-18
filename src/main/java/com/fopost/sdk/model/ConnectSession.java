package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * A hosted connect link. {@code url} is present on creation only; {@code accountId} is set once
 * the session is used. {@code status} is {@code pending}, {@code used} or {@code expired}.
 */
public record ConnectSession(
        String id,
        String status,
        String externalUserId,
        List<String> platforms,
        String redirectUrl,
        Branding branding,
        String accountId,
        String url,
        Instant expiresAt,
        Instant usedAt,
        Instant createdAt) {

    /** What the hosted page shows as yours. */
    public record Branding(String name, String logoUrl) {}
}
