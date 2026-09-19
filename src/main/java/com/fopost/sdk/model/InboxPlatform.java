package com.fopost.sdk.model;

/** Which networks feed the inbox. {@code comments} and {@code dms} are live, soon or none. */
public record InboxPlatform(String platform, String comments, String dms) {}
