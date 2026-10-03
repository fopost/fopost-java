package com.fopost.sdk.model;

/** The pagination block a broadcast, recipient, sequence or enrollment listing carries. */
public record BroadcastPageMeta(int page, int perPage, long total) {}
