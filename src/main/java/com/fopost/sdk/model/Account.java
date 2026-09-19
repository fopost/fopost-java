package com.fopost.sdk.model;

import java.time.Instant;

/**
 * A connected social account.
 *
 * <p>Named {@code Account} for the platform account, not the billing account. Which fields
 * arrive depends on the endpoint: the list gives health, primary state and
 * {@code reconnectRequired}, the detail gives timestamps and the owning workspace.
 *
 * <p>{@code reconnectRequired} is true when the account was connected before a permission it
 * now needs was asked for; reconnecting it is the fix. {@code name} is the display name when one is set, and
 * {@code platformName} is always the name from the platform.
 */
public record Account(
        String id,
        String workspaceId,
        String platform,
        String username,
        String name,
        String avatar,
        Boolean isPrimary,
        Boolean active,
        String healthStatus,
        Instant lastHealthCheck,
        WorkspaceRef workspace,
        Instant createdAt,
        Instant updatedAt,
        String platformName) {}
