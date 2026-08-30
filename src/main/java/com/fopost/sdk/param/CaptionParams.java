package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Inputs for caption generation. Every field is optional, but give it something to work from. */
public final class CaptionParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    public static CaptionParams create() {
        return new CaptionParams();
    }

    /** The draft to improve, when there is one. */
    public CaptionParams currentCaption(String currentCaption) {
        body.put("current_caption", currentCaption);
        return this;
    }

    /** Images to describe. They must be reachable by the API. */
    public CaptionParams imageUrls(List<String> imageUrls) {
        body.put("image_urls", imageUrls);
        return this;
    }

    public CaptionParams platforms(List<String> platforms) {
        body.put("platforms", platforms);
        return this;
    }

    public CaptionParams platforms(String... platforms) {
        return platforms(List.of(platforms));
    }

    public CaptionParams charLimit(int charLimit) {
        body.put("char_limit", charLimit);
        return this;
    }

    public CaptionParams workspaceId(String workspaceId) {
        body.put("workspace_id", workspaceId);
        return this;
    }

    /** Write in one of the workspace's brand voices. */
    public CaptionParams brandVoiceId(String brandVoiceId) {
        body.put("brand_voice_id", brandVoiceId);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
