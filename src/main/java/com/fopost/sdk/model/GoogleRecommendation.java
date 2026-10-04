package com.fopost.sdk.model;

/**
 * One of Google's own recommendations for the account.
 *
 * <p>{@code id} is the Google resource name rather than the {@code ~} form other objects use,
 * because a recommendation is not an object you address again: it is what apply and dismiss take.
 */
public record GoogleRecommendation(
        String id,
        String type,
        String campaignId,
        String adGroupId,
        boolean dismissed,
        GoogleRecommendationImpact impact) {}
