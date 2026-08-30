package com.fopost.sdk.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Builds a {@code multipart/form-data} body, for the two endpoints that take files. */
public final class Multipart {

    private final String boundary = "----fopost" + UUID.randomUUID().toString().replace("-", "");
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    public String contentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    public Multipart field(String name, String value) {
        if (value == null) {
            return this;
        }
        write("--" + boundary + "\r\n");
        write("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
        write(value);
        write("\r\n");
        return this;
    }

    public Multipart file(String name, String filename, String contentType, byte[] content) {
        write("--" + boundary + "\r\n");
        write("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"\r\n");
        write("Content-Type: " + (contentType == null ? "application/octet-stream" : contentType) + "\r\n\r\n");
        writeBytes(content);
        write("\r\n");
        return this;
    }

    public byte[] build() {
        write("--" + boundary + "--\r\n");
        return out.toByteArray();
    }

    private void write(String text) {
        writeBytes(text.getBytes(StandardCharsets.UTF_8));
    }

    private void writeBytes(byte[] bytes) {
        try {
            out.write(bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
