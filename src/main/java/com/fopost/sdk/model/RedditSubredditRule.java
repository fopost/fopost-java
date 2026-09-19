package com.fopost.sdk.model;

/** One rule a subreddit publishes. {@code appliesTo} is {@code link}, {@code comment} or {@code all}. */
public record RedditSubredditRule(String name, String description, String appliesTo) {}
