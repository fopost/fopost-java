package com.fopost.sdk.model;

/** A channel the Slack app can post to; {@code isCurrent} marks the one this account posts to. */
public record SlackChannel(String id, String name, boolean isPrivate, boolean isMember, boolean isCurrent) {}
