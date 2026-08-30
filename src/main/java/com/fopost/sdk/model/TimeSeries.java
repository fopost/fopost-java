package com.fopost.sdk.model;

import java.util.List;

/** Daily activity over the requested window. */
public record TimeSeries(Integer days, List<Point> series) {

    public record Point(
            String date,
            Long engagements,
            Long impressions,
            Long likes,
            Long comments,
            Long shares,
            Long followers,
            Long posts) {}
}
