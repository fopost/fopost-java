package com.fopost.sdk.model;

import java.util.List;

/**
 * What only this network reports, in its own vocabulary: ad-break earnings, story taps, a
 * retention curve, the search terms behind a listing.
 */
public record AccountPlatformMetrics(String platform, Block account, Block post) {

    /**
     * One side of the set: the account itself, or its newest measured post.
     * {@code externalPostId} is null on the account side.
     */
    public record Block(String fetchedAt, String externalPostId, List<Row> metrics) {}

    /**
     * One metric a network reports under its own name. {@code key} is the platform's own name
     * and is stable; {@code label} is ours and may be reworded, so match on {@code key}.
     * {@code kind} is one of count, duration_ms, currency_usd, ratio, series.
     *
     * <p>{@code value} is a {@link Number} for every kind but series, which is a list of points,
     * so it is declared as {@link Object} and {@link #number()} decodes the common case.
     */
    public record Row(String key, String label, String kind, Object value) {

        /** The value as a number, or null for a series or a non-numeric answer. */
        public Double number() {
            return value instanceof Number n ? n.doubleValue() : null;
        }
    }
}
