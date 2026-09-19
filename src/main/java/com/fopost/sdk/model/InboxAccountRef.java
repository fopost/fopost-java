package com.fopost.sdk.model;

/** The connected account an inbox item, thread or conversation belongs to. */
public record InboxAccountRef(String id, String platform, String username, String name, String avatar) {}
