package com.fopost.sdk.model;

/** What a conversion rule recorded over a date range. */
public record ConversionMetrics(
        Integer conversions,
        Integer postClickConversions,
        Integer viewThroughConversions,
        Long valueMinor,
        Long costPerConversionMinor) {}
