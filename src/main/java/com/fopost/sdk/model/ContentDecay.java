package com.fopost.sdk.model;

import java.util.List;

/**
 * How engagement accumulates as a post ages, from the repeated readings taken of every post.
 *
 * <p>{@code halfLifeBucket} names the first band where the average post had passed half its final
 * engagement, and is null when nothing was measured.
 */
public record ContentDecay(Integer days, Integer postsMeasured, String halfLifeBucket, List<Band> bands) {

    /**
     * One age band. {@code posts} counts the posts with at least one reading in it, and
     * {@code shareOfFinal} is null when nothing in the band had earned anything yet.
     */
    public record Band(
            String bucket,
            String label,
            Integer posts,
            Double avgEngagements,
            Double avgImpressions,
            Double shareOfFinal) {}
}
