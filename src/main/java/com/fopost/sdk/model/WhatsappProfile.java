package com.fopost.sdk.model;

import java.util.List;

/**
 * The business profile on a WhatsApp number, plus how the platform rates it.
 * {@code displayNameStatus} is the platform's review state for the display name.
 */
public record WhatsappProfile(
        String about,
        String address,
        String description,
        String email,
        String vertical,
        List<String> websites,
        String profilePictureUrl,
        String displayName,
        String displayNameStatus,
        String username,
        String qualityRating,
        String messagingLimitTier) {}
