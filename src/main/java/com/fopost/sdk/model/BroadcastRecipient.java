package com.fopost.sdk.model;

import java.time.Instant;

/**
 * One contact on one broadcast, and what became of their message.
 *
 * <p>{@code status} is {@code pending}, {@code sent}, {@code skipped} or {@code failed}.
 * {@code skipReason} is set when the status is {@code skipped}: {@code window_closed},
 * {@code no_conversation} or {@code unsupported_platform}. {@code window_closed} means the
 * network's messaging window had shut, so nothing was attempted.
 */
public record BroadcastRecipient(
        String contactId,
        String displayName,
        String status,
        String skipReason,
        Instant sentAt,
        String error) {}
