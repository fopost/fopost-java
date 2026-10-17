package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * Every reading held for one post, oldest first, one timeline per delivery, because the same post
 * on two networks decays differently.
 *
 * <p>{@code postId} is null when the post was made natively on the network.
 */
public record PostTimeline(String postId, List<Delivery> deliveries) {

    public record Delivery(
            String accountId,
            String platform,
            String username,
            String externalPostId,
            Instant postedAt,
            List<Point> points) {}

    /**
     * One reading. {@code ageMinutes} is null when the network never said when the post went out,
     * and {@code delta} is what moved since the reading before this one.
     */
    public record Point(
            Instant at,
            Integer ageMinutes,
            Integer impressions,
            Integer reach,
            Integer engagements,
            Integer likes,
            Integer comments,
            Integer shares,
            Integer videoViews,
            Delta delta) {}

    public record Delta(
            Integer impressions, Integer reach, Integer engagements, Integer likes, Integer comments, Integer shares) {}
}
