package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** A named set of accounts in one workspace, for posting to all of them at once. */
public record AccountGroup(
        String id,
        String name,
        List<String> accountIds,
        Instant createdAt,
        Instant updatedAt) {}
