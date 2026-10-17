package com.fopost.sdk.model;

/**
 * What a send started.
 *
 * <p>{@code recipients} is how many contacts matched, not how many will be messaged — the
 * messaging window decides that.
 */
public record BroadcastSent(String id, String status, int recipients) {}
