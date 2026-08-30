package com.fopost.sdk.model;

/** A media attachment on a content block. {@code type} is {@code image}, {@code video} or {@code gif}. */
public record MediaItem(
        String type,
        String name,
        String url,
        Long size,
        String alt,
        String thumbnail) {

    public static MediaItem of(String type, String name, String url) {
        return new MediaItem(type, name, url, null, null, null);
    }
}
