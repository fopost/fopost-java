package com.fopost.sdk.model;

/** How many posts a bulk shift, relabel, or delete touched. */
public record BulkActionResult(Integer updated, String action, String mode) {}
