package com.fopost.sdk.model;

/**
 * What became of a broadcast's recipients, by status.
 *
 * <p>{@code skipped} is usually the messaging window doing its job.
 */
public record BroadcastCounts(int total, int sent, int skipped, int failed, int pending) {}
