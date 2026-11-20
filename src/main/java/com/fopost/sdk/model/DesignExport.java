package com.fopost.sdk.model;

import java.util.List;

/**
 * The files an export rendered, each already in the media library.
 *
 * @param format {@code png}, {@code jpg}, {@code pdf}, {@code mp4} or {@code gif}
 */
public record DesignExport(String format, List<File> files) {

    /**
     * @param pageIndex the page this file shows; null for a file covering the whole design
     */
    public record File(String mediaId, Integer pageIndex, String mimeType, String previewUrl) {}
}
