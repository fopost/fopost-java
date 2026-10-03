package com.fopost.sdk.model;

/** One page of catalog products; pass {@code nextCursor} back as {@code after}. */
public record CatalogProductsPage(
        java.util.List<CatalogProduct> products,
        String nextCursor) {}
