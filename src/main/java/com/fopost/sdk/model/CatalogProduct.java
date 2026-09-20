package com.fopost.sdk.model;

/** One product in a catalog. {@code priceMinor} is minor units of {@code currency}. */
public record CatalogProduct(
        String id,
        String retailerId,
        String name,
        String description,
        String availability,
        String condition,
        Long priceMinor,
        String currency,
        String imageUrl,
        String url) {}
