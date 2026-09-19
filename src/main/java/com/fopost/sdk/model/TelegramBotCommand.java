package com.fopost.sdk.model;

/** One entry in a Telegram bot's command menu; {@code command} is 1-32 of {@code [a-z0-9_]}. */
public record TelegramBotCommand(String command, String description) {}
