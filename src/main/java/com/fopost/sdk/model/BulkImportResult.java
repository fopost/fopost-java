package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/** What a committed bulk-import batch created. Keep {@code batchId} to roll the batch back. */
public record BulkImportResult(String batchId, Integer created, List<CreatedPost> posts) {

    public record CreatedPost(String id, Instant scheduleAt) {}
}
