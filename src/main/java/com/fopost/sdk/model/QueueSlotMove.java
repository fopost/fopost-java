package com.fopost.sdk.model;

import java.time.Instant;

/** A queued post moved to a new time because its slot changed. {@code id} is the post id. */
public record QueueSlotMove(String id, Instant from, Instant to) {}
