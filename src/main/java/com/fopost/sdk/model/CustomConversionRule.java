package com.fopost.sdk.model;

/** The pixel event a custom conversion counts, narrowed by one URL condition. */
public record CustomConversionRule(String eventName, String urlContains, String urlEquals) {}
