package com.fopost.sdk.model;

/** What a catalog product batch was accepted as. */
public record CatalogBatchResult(
        java.util.List<String> handles,
        long accepted) {}
