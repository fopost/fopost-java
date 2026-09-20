package com.fopost.sdk.model;

/** Keeps a catalog in step with a product file you host. */
public record ProductFeed(
        String id,
        String name,
        String url,
        String schedule,
        String createdAt) {}
