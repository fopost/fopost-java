package com.fopost.sdk.model;

import java.util.List;
import java.util.Map;

/**
 * The answer to a publish request.
 *
 * <p>A dry run fills {@code dryRun} and {@code post} and queues nothing; a real publish fills
 * {@code postStatus} and {@code deliveries}.
 */
public record PublishResult(
        String postStatus,
        List<Delivery> deliveries,
        List<HealthWarning> healthWarnings,
        Boolean dryRun,
        Map<String, Object> post) {

    /** True when this was a dry run, so nothing was queued. */
    public boolean isDryRun() {
        return Boolean.TRUE.equals(dryRun);
    }
}
