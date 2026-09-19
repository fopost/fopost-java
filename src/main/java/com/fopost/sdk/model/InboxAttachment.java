package com.fopost.sdk.model;

/** A file on a direct message. {@code url} and {@code previewUrl} are served by the API, never a platform url. */
public record InboxAttachment(
        String kind,
        String name,
        Integer width,
        Integer height,
        String link,
        String url,
        String previewUrl) {}
