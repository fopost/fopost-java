package com.fopost.sdk.model;

import java.util.List;

/** A subreddit's rules, in its own order. Show them before publishing. */
public record RedditSubredditRules(String subreddit, List<RedditSubredditRule> rules) {}
