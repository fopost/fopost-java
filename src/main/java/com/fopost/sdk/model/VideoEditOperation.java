package com.fopost.sdk.model;

import java.util.Map;

/**
 * One step of a video edit, such as {@code trim}, {@code subtitles} or {@code reframe}.
 *
 * @param params the step's parameters exactly as the API names them, for example
 *     {@code Map.of("startSec", 2, "endSec", 12)} for {@code trim}; sent unchanged
 */
public record VideoEditOperation(String op, Map<String, Object> params) {

    public static VideoEditOperation of(String op, Map<String, Object> params) {
        return new VideoEditOperation(op, params);
    }

    public static VideoEditOperation of(String op) {
        return new VideoEditOperation(op, Map.of());
    }
}
