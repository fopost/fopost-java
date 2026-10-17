package com.fopost.sdk.model;

import java.time.Instant;

/**
 * The last keyword research run. {@code status} is {@code queued}, {@code running}, {@code done} or
 * {@code failed}; {@code added} is how many new keywords it found.
 */
public record BlogPlanRun(String status, String error, Integer added, Instant finishedAt) {}
