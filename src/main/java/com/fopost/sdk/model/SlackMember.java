package com.fopost.sdk.model;

/** A person in the connected Slack workspace; pass {@code id} as the handle to start a DM. */
public record SlackMember(
        String id, String name, String realName, String displayName, String avatar, boolean isBot) {}
