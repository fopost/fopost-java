package com.fopost.sdk.model;

import java.util.List;

/**
 * Weekly posting cadence set against what each cadence earned per post.
 *
 * <p>Weeks run Monday to Sunday in UTC and are grouped into bands by their own post count, so a
 * four-post week is compared against other four-post weeks. {@code best} is null without posts.
 */
public record PostingFrequency(Integer days, List<Week> weeks, List<Band> bands, Band best) {

    /** One week of posting. {@code weekStart} is the Monday, UTC, as YYYY-MM-DD. */
    public record Week(String weekStart, Integer posts, Integer engagements, Double avgEngagementsPerPost) {}

    /**
     * The weeks that shared a cadence, folded together. {@code engagementRate} is engagements over
     * reach, impressions as the stand-in, and null with neither.
     */
    public record Band(
            String band,
            String label,
            Integer weeks,
            Integer posts,
            Double avgPostsPerWeek,
            Double avgEngagementsPerPost,
            Double engagementRate) {}
}
