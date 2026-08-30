package com.fopost.sdk.model;

import java.util.List;

/**
 * Audience demographics, aggregated over the accounts that report them.
 *
 * <p>{@code unsupportedAccounts} names the accounts whose platform returns no demographics,
 * so a thin result is explainable rather than surprising.
 */
public record Demographics(
        String audience,
        Dimensions dimensions,
        List<AccountRef> contributingAccounts,
        List<AccountRef> unsupportedAccounts) {

    public record Dimensions(
            List<Bucket> age,
            List<Bucket> gender,
            List<Bucket> country,
            List<Bucket> city) {}

    /** One demographic bucket: its label, its absolute value, and its share of the total. */
    public record Bucket(String key, Double value, Double share) {}

    public record AccountRef(String accountId, String platform, String username) {}
}
