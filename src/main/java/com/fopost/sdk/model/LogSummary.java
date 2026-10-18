package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** Totals and hourly buckets for the whole filtered window, not just the page. */
public record LogSummary(Instant since, Integer total, Integer errors, List<Bucket> buckets) {

    public record Bucket(Instant at, Integer total, Integer errors) {}
}
