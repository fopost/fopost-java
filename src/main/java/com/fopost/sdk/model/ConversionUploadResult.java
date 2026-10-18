package com.fopost.sdk.model;

/**
 * What a conversion upload came back with. {@code replayed} is true when the
 * {@code Idempotency-Key} had been used before and nothing was sent again.
 */
public record ConversionUploadResult(String id, long accepted, long eventCount, boolean replayed) {}
