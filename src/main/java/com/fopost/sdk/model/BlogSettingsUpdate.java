package com.fopost.sdk.model;

/**
 * The answer to a settings update. {@code websiteSecret} is set only when the update set
 * {@code websiteUrl} for the first time, and is never shown again: store it then.
 */
public record BlogSettingsUpdate(BlogSettings settings, String websiteSecret) {}
