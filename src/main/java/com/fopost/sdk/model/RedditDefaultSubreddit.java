package com.fopost.sdk.model;

/**
 * Where posts from a Reddit account go when a post names no subreddit.
 *
 * <p>A null {@code subreddit} means the account's own profile page, which always accepts a post.
 */
public record RedditDefaultSubreddit(String subreddit) {}
