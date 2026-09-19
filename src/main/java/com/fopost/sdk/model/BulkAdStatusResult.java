package com.fopost.sdk.model;

/** The outcome for one object of a bulk status change. {@code error} is null when it worked. */
public record BulkAdStatusResult(String id, String level, Boolean ok, String error) {}
