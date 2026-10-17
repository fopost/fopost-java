package com.fopost.sdk.model;

import java.time.Instant;

/**
 * A group on the business number. Participation is invite-only: no endpoint adds
 * someone, so {@code inviteLink} is how they join.
 */
public record WhatsappGroup(
        String id,
        String subject,
        String description,
        Integer participantCount,
        String inviteLink,
        Instant createdAt) {}
