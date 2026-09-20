package com.fopost.sdk.model;

/** The outcome of a Messenger hand-over; {@code appId} is null when control was taken back. */
public record InboxHandover(String appId, String control) {}
