package com.fopost.sdk.model;

/** A product catalog on the connection's business portfolio, read live. */
public record ProductCatalog(
        String id,
        String name,
        String vertical,
        Long productCount) {}
