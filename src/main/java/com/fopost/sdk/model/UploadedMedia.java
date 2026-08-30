package com.fopost.sdk.model;

/** A file as returned by an upload, ready to attach to a content block. */
public record UploadedMedia(String id, String type, String name, String url, Long size) {

    /** The same file shaped as a content-block attachment. */
    public MediaItem toMediaItem() {
        return new MediaItem(type, name, url, size, null, null);
    }
}
