package com.fopost.sdk.model;

import java.time.LocalDate;
import java.util.List;

/**
 * How the social inbox performed over the period. Aggregates only. {@code deltas} are percentage
 * changes against the previous period of the same length; {@code heatmap} is bucketed in
 * {@code timeZone}.
 */
public record InboxAnalytics(
        Totals totals,
        Deltas deltas,
        List<Point> series,
        List<PlatformRow> byPlatform,
        List<TypeRow> byType,
        List<AccountRow> byAccount,
        List<ResponseTimeRow> responseTime,
        List<HeatmapCell> heatmap,
        String timeZone) {

    /** {@code medianResponseMinutes} is null when nothing was replied to. */
    public record Totals(
            Integer received,
            Integer sent,
            Integer answered,
            Integer open,
            Integer conversations,
            Double medianResponseMinutes) {}

    public record Deltas(Double received, Double sent, Double answered) {}

    public record Point(LocalDate date, Integer received, Integer sent, Integer answered) {}

    public record PlatformRow(String platform, Integer received, Integer sent) {}

    /** {@code type} is comment, mention, reply, dm or review. */
    public record TypeRow(String type, Integer count) {}

    public record AccountRow(
            String accountId,
            String platform,
            String username,
            String name,
            String avatar,
            Integer received,
            Integer sent) {}

    /** {@code bucket} is under_1h, 1h_6h, 6h_24h, 1d_3d or over_3d. */
    public record ResponseTimeRow(String bucket, Integer count) {}

    /** {@code weekday} 0 is Sunday; {@code hour} is 0 to 23. */
    public record HeatmapCell(Integer weekday, Integer hour, Integer count) {}
}
