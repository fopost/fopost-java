package com.fopost.sdk.model;

/**
 * What Google projects applying a recommendation would change. A null field is one Google does
 * not estimate for that recommendation.
 */
public record GoogleRecommendationImpact(
        Double baseClicks,
        Double potentialClicks,
        /** The account's currency, in minor units. */
        Long baseCostMinor,
        Long potentialCostMinor,
        Double baseConversions,
        Double potentialConversions) {}
