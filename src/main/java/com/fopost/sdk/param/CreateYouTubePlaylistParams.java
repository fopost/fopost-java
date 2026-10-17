package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** A new playlist. {@code privacy} is public, unlisted or private; unset means private. */
public final class CreateYouTubePlaylistParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateYouTubePlaylistParams(String title) {
        body.put("title", title);
    }

    public static CreateYouTubePlaylistParams of(String title) {
        return new CreateYouTubePlaylistParams(title);
    }

    public CreateYouTubePlaylistParams description(String description) {
        body.put("description", description);
        return this;
    }

    public CreateYouTubePlaylistParams privacy(String privacy) {
        body.put("privacy", privacy);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
