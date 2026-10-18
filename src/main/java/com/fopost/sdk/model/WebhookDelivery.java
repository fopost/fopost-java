package com.fopost.sdk.model;

import java.time.Instant;

/**
 * One attempt to deliver an event to a webhook. Every attempt of one event shares
 * {@code deliveryKey} (the {@code X-FoPost-Delivery} id); {@code attempt} is 1 for the first send
 * and counts up through retries and redeliveries. {@code status} is {@code success} or
 * {@code failed}.
 */
public record WebhookDelivery(
        String id,
        String webhookId,
        String event,
        String deliveryKey,
        Integer attempt,
        String status,
        Integer responseStatus,
        String responseSnippet,
        Long durationMs,
        String payloadHash,
        Instant nextRetryAt,
        Instant createdAt) {}
