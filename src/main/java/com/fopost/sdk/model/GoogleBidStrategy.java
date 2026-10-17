package com.fopost.sdk.model;

/** A portfolio bid strategy on the account. */
public record GoogleBidStrategy(
        String id, String name, String type, String status, int campaignCount) {}
