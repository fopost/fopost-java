package com.fopost.sdk.model;

/**
 * Where a Telegram connect code stands: {@code pending}, {@code connected} (with {@code accountId}),
 * {@code failed} (with {@code reason}) or {@code expired}.
 */
public record TelegramConnectStatus(String status, String accountId, String reason) {}
