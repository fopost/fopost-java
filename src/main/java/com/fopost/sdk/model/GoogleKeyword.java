package com.fopost.sdk.model;

/**
 * A keyword on an ad group.
 *
 * <p>{@code id} is {@code <customerId>~keyword~<adGroupId>~<criterionId>}: a Google resource name
 * has slashes and cannot ride in a URL path segment, so every id here carries the account it
 * belongs to.
 */
public record GoogleKeyword(
        String id,
        String adGroupId,
        String text,
        String matchType,
        String status,
        /** The account's currency, in minor units. */
        Long cpcBidMinor,
        boolean negative) {}
