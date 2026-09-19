package com.fopost.sdk.model;

/** A new lead Page subscription. {@code backfilled} counts the existing leads stored with it. */
public record LeadPageSubscription(String pageId, Integer backfilled) {}
