package com.fopost.sdk.model;

/** A conversion action on the account. */
public record GoogleConversionAction(
        String id,
        String name,
        String category,
        String status,
        String type,
        String countingType,
        /** The account's currency, in minor units. */
        Long valueMinor) {}
