package com.fopost.sdk.model;

import java.util.List;

/** Google's estimate of how well the account is set up, from 0 to 1. */
public record GoogleOptimizationScore(
        Double score,
        /** How much this account's score counts against others under the same manager. */
        Double weight,
        List<GoogleOptimizationScoreCampaign> campaigns) {}
