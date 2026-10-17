package com.fopost.sdk.model;

/** Weights conversions so some audiences count for more than others. */
public record ValueRuleSet(
        String id,
        String name,
        String status,
        java.util.List<ValueRule> rules) {}
