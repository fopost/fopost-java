package com.fopost.sdk.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Builds a {@code multipart/form-data} body for the endpoints that take files. File parts are
 * streamed from their source when the request is sent, so the body is never held in memory whole.
 */
public final class Multipart {

    private final String boundary = "----fopost" + UUID.randomUUID().toString().replace("-", "");
    private final List<Supplier<InputStream>> parts = new ArrayList<>();
    private long length;

    public String contentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    public Multipart field(String name, String value) {
        if (value == null) {
            return this;
        }
        text("--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n"
                + value + "\r\n");
        return this;
    }

    public Multipart file(String name, String filename, String contentType, byte[] content) {
        return file(name, filename, contentType, () -> new ByteArrayInputStream(content), content.length);
    }

    public Multipart file(String name, String filename, String contentType, Supplier<InputStream> content, long size) {
        text("--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"\r\n"
                + "Content-Type: " + (contentType == null ? "application/octet-stream" : contentType) + "\r\n\r\n");
        parts.add(content);
        length += size;
        text("\r\n");
        return this;
    }

    /** Total body length including the closing boundary, for {@code Content-Length}. */
    public long length() {
        return length + closing().length;
    }

    /** The whole body as one stream; each file part is opened as the stream reaches it. */
    public InputStream open() {
        List<InputStream> streams = new ArrayList<>(parts.size() + 1);
        for (Supplier<InputStream> part : parts) {
            streams.add(new LazyInputStream(part));
        }
        streams.add(new ByteArrayInputStream(closing()));
        return new SequenceInputStream(Collections.enumeration(streams));
    }

    private byte[] closing() {
        return ("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);
    }

    private void text(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        parts.add(() -> new ByteArrayInputStream(bytes));
        length += bytes.length;
    }

    /** Defers opening a file until the preceding parts have been consumed. */
    private static final class LazyInputStream extends InputStream {

        private final Supplier<InputStream> source;
        private InputStream delegate;

        private LazyInputStream(Supplier<InputStream> source) {
            this.source = source;
        }

        private InputStream delegate() {
            if (delegate == null) {
                delegate = source.get();
            }
            return delegate;
        }

        @Override
        public int read() throws java.io.IOException {
            return delegate().read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws java.io.IOException {
            return delegate().read(b, off, len);
        }

        @Override
        public void close() throws java.io.IOException {
            if (delegate != null) {
                delegate.close();
            }
        }
    }
}
