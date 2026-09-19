package com.fopost.sdk.model;

/**
 * A post flair, valid only in the subreddit it came from.
 *
 * <p>{@code editable} means the label may be replaced with your own text.
 */
public record RedditFlair(String id, String text, Boolean editable) {}
