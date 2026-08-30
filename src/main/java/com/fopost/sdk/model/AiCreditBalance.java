package com.fopost.sdk.model;

import java.time.Instant;

/** The AI credit balance for the current billing period. */
public record AiCreditBalance(
        Integer creditsRemaining,
        Integer creditsUsed,
        Integer creditsTotal,
        Instant periodStart,
        Instant periodEnd) {}
