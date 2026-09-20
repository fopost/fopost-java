package com.fopost.sdk.model;

/** The slice of a catalog one catalog ad runs from. */
public record ProductSet(
        String id,
        String name,
        Long productCount,
        java.util.Map<String, Object> filter) {}
