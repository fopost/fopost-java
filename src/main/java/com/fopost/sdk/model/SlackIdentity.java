package com.fopost.sdk.model;

/** The name and icon a Slack account posts under; each is null when unset. */
public record SlackIdentity(String username, String iconUrl, String iconEmoji) {}
