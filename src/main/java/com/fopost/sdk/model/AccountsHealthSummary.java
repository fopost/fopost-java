package com.fopost.sdk.model;

import java.util.List;

/** Health for every account the key can reach, with the counts rolled up. */
public record AccountsHealthSummary(List<AccountHealth> accounts, Counts summary) {

    public record Counts(
            Integer total,
            Integer healthy,
            Integer degraded,
            Integer expired,
            Integer revoked,
            Integer unknown) {}
}
