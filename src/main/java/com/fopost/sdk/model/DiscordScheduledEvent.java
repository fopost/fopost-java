package com.fopost.sdk.model;

/**
 * An event on the server's calendar. {@code channelId} names a voice or stage channel; otherwise
 * {@code location} says where it happens. {@code status} is scheduled, active, completed or canceled.
 */
public record DiscordScheduledEvent(
        String id,
        String name,
        String description,
        String channelId,
        String location,
        String startTime,
        String endTime,
        String status,
        Integer userCount) {}
