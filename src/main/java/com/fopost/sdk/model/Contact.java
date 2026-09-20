package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * One person, however many handles they write from.
 *
 * <p>{@code source} is {@code inbox}, {@code radar} or {@code import} — what first created
 * the row. {@code workspaceId} is set only on a listing that spans workspaces.
 */
public record Contact(
        String id,
        String displayName,
        List<ContactChannel> channels,
        String source,
        String note,
        Instant firstSeenAt,
        Instant lastSeenAt,
        Map<String, String> fields,
        List<ContactLabel> labels,
        String workspaceId) {

    public Contact {
        channels = channels == null ? List.of() : List.copyOf(channels);
        labels = labels == null ? List.of() : List.copyOf(labels);
        fields = fields == null ? Map.of() : Map.copyOf(fields);
    }
}
