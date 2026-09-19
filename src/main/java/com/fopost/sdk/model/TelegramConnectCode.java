package com.fopost.sdk.model;

import java.time.Instant;

/** A one-time code that connects a Telegram chat when sent to the bot as {@code command}. */
public record TelegramConnectCode(
        String code,
        String command,
        String botUsername,
        String deepLink,
        String groupLink,
        Instant expiresAt) {}
