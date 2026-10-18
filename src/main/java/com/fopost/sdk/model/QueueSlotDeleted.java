package com.fopost.sdk.model;

import java.util.List;

/** The answer to deleting a slot: the queued posts that were moved to the next free slot. */
public record QueueSlotDeleted(Boolean deleted, List<QueueSlotMove> moved) {}
