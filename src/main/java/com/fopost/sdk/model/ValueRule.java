package com.fopost.sdk.model;

/** Weights one condition's conversions. */
public record ValueRule(
        String condition,
        Double multiplier) {}
