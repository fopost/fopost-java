package com.fopost.sdk.model;

/** A keyword idea, or the historical metrics of a keyword you already have. */
public record GoogleKeywordIdea(
        String text,
        long avgMonthlySearches,
        String competition,
        /** The account's currency, in minor units. */
        Long lowTopOfPageBidMinor,
        Long highTopOfPageBidMinor) {}
