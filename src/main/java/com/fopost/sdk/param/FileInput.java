package com.fopost.sdk.param;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * A file to upload: its name, MIME type, exact size, and where the bytes come from.
 *
 * <pre>{@code
 * FileInput.of(Path.of("launch.mp4"));
 * FileInput.of("chart.png", "image/png", bytes);
 * FileInput.of("export.mov", "video/quicktime", inputStream, sizeInBytes);
 * }</pre>
 *
 * <p>A {@link Path} is reopened for each request; an {@link InputStream} can be sent once, so a
 * request that is retried after it has been read fails.
 */
public final class FileInput {

    private final String filename;
    private final String mimeType;
    private final long size;
    private final Supplier<InputStream> source;

    private FileInput(String filename, String mimeType, long size, Supplier<InputStream> source) {
        this.filename = filename;
        this.mimeType = mimeType == null ? "application/octet-stream" : mimeType;
        this.size = size;
        this.source = source;
    }

    public static FileInput of(Path path) {
        return of(path, guessMimeType(path.getFileName().toString()));
    }

    public static FileInput of(Path path, String mimeType) {
        long size;
        try {
            size = Files.size(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return new FileInput(path.getFileName().toString(), mimeType, size, () -> {
            try {
                return Files.newInputStream(path);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public static FileInput of(String filename, String mimeType, byte[] content) {
        return new FileInput(filename, mimeType, content.length, () -> new ByteArrayInputStream(content));
    }

    /** {@code size} must be the exact number of bytes the stream yields. */
    public static FileInput of(String filename, String mimeType, InputStream content, long size) {
        boolean[] opened = new boolean[1];
        return new FileInput(filename, mimeType, size, () -> {
            if (opened[0]) {
                throw new IllegalStateException("fopost: an InputStream file can only be sent once");
            }
            opened[0] = true;
            return content;
        });
    }

    public String filename() {
        return filename;
    }

    public String mimeType() {
        return mimeType;
    }

    public long size() {
        return size;
    }

    public Supplier<InputStream> source() {
        return source;
    }

    /** The content-block media type for this file: {@code gif}, {@code image} or {@code video}. */
    public String mediaType() {
        if (mimeType.equalsIgnoreCase("image/gif")) {
            return "gif";
        }
        return mimeType.toLowerCase().startsWith("video/") ? "video" : "image";
    }

    static String guessMimeType(String filename) {
        String guessed = URLConnection.guessContentTypeFromName(filename);
        return guessed == null ? "application/octet-stream" : guessed;
    }
}
