package com.fopost.sdk.model;

import java.time.Instant;
import java.time.LocalDate;

/**
 * A keyword in the plan. {@code status} is one of {@link BlogKeywordStatus}; {@code source} is
 * {@code provider}, {@code user} or {@code agent}. {@code difficulty} runs 0 to 100.
 * {@code fitReason} says why the keyword serves the blog goal.
 */
public record BlogKeyword(
        String id,
        String keyword,
        Integer searchVolume,
        Integer difficulty,
        Double cpc,
        String intent,
        String source,
        String status,
        LocalDate plannedFor,
        String fitReason,
        Instant createdAt) {}
