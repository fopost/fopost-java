package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** A workspace. {@code accounts} is populated by the list and get endpoints. */
public record Workspace(
        String id,
        String name,
        String slug,
        String type,
        String logo,
        String website,
        String timezone,
        String country,
        String description,
        String language,
        Boolean requireApproval,
        Boolean aiAltTextEnabled,
        String brandColor,
        List<Account> accounts,
        Instant createdAt,
        Instant updatedAt) {}
