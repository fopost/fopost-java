package com.fopost.sdk.model;

import java.util.List;

/** The command menu the bot shows in a connected Telegram chat. */
public record TelegramBotCommands(List<TelegramBotCommand> commands) {}
