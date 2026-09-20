package com.fopost.sdk.model;

/** A creator who allowlisted this advertiser for partnership ads. */
public record PartnershipCreator(
        String id,
        String username,
        String name,
        String status,
        java.util.List<String> permissions) {}
