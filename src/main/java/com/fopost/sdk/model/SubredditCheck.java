package com.fopost.sdk.model;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * Whether a subreddit exists and takes a post from a named Reddit account.
 *
 * <p>{@code ok} is true when both hold. A private, banned or missing subreddit answers 200 with
 * {@code exists} false rather than an error.
 */
public record SubredditCheck(
        String subreddit,
        Boolean exists,
        Boolean canPost,
        // Neither the snake strategy nor the camel alias covers a digit boundary.
        @JsonAlias("over_18") Boolean over18,
        Boolean flairEnabled,
        Boolean ok) {}
