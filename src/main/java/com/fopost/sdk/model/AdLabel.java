package com.fopost.sdk.model;

/** Groups campaigns, ad sets and ads for reporting. */
public record AdLabel(
        String id,
        String name,
        String createdAt) {}
