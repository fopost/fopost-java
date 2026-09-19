package com.fopost.sdk.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A draft to check against one or more platforms.
 *
 * <pre>{@code
 * ValidatePostParams.of("twitter", "linkedin")
 *     .content("Shipping today")
 *     .media("https://cdn.example.test/chart.png", "image/png");
 * }</pre>
 */
public final class ValidatePostParams {

    private final List<String> platforms;
    private String content;
    private final List<Map<String, Object>> media = new ArrayList<>();

    private ValidatePostParams(List<String> platforms) {
        this.platforms = List.copyOf(platforms);
    }

    public static ValidatePostParams of(List<String> platforms) {
        return new ValidatePostParams(platforms);
    }

    public static ValidatePostParams of(String... platforms) {
        return of(List.of(platforms));
    }

    public ValidatePostParams content(String content) {
        this.content = content;
        return this;
    }

    public ValidatePostParams media(String url, String mimeType) {
        return media(url, mimeType, null);
    }

    /** {@code size} is in bytes and optional. */
    public ValidatePostParams media(String url, String mimeType, Long size) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("url", url);
        item.put("mime_type", mimeType);
        Params.put(item, "size", size);
        media.add(item);
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> body = new LinkedHashMap<>();
        Params.put(body, "content", content);
        if (!media.isEmpty()) {
            body.put("media", List.copyOf(media));
        }
        body.put("platforms", platforms);
        return body;
    }
}
