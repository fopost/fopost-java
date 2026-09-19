package com.fopost.sdk.model;

/**
 * A subreddit a Reddit account is in, or its own profile page.
 *
 * <p>{@code name} carries no {@code r/} prefix. {@code canPost} is false where the account may
 * read but not submit, and {@code isDefault} marks where posts go when a post names no subreddit.
 */
public record RedditSubreddit(
        String name,
        String title,
        Long subscribers,
        Boolean over18,
        Boolean canPost,
        Boolean flairEnabled,
        String iconUrl,
        Boolean isDefault) {}
