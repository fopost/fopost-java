package com.fopost.sdk.model;

/** A DM opened by handle or as a private reply: its thread and the message sent. */
public record InboxConversationStarted(String conversationId, InboxItem item) {}
