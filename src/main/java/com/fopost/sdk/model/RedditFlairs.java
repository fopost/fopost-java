package com.fopost.sdk.model;

import java.util.List;

/** The post flairs one subreddit offers. */
public record RedditFlairs(String subreddit, List<RedditFlair> flairs) {}
