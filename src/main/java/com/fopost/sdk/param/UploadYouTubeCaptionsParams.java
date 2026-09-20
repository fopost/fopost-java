package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A caption track to upload. {@code body} is the subtitle file itself; YouTube reads SRT and WebVTT
 * and works out which from the bytes, so the format is not declared.
 */
public final class UploadYouTubeCaptionsParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private UploadYouTubeCaptionsParams(String language, String subtitles) {
        body.put("language", language);
        body.put("body", subtitles);
    }

    /** {@code language} is a BCP-47 tag. */
    public static UploadYouTubeCaptionsParams of(String language, String subtitles) {
        return new UploadYouTubeCaptionsParams(language, subtitles);
    }

    public UploadYouTubeCaptionsParams name(String name) {
        body.put("name", name);
        return this;
    }

    public UploadYouTubeCaptionsParams isDraft(boolean isDraft) {
        body.put("is_draft", isDraft);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
