package com.fopost.sdk.model;

import java.util.List;

/** One year of posting activity, a row per day. */
public record PostingStreak(List<Day> streak) {

    public record Day(
            String date,
            Integer count,
            Integer publishedCount,
            Integer failedCount,
            Integer scheduledCount) {}
}
