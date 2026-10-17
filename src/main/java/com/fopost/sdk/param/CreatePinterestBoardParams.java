package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** A new Pinterest board. {@code privacy} is PUBLIC, PROTECTED or SECRET; unset means PUBLIC. */
public final class CreatePinterestBoardParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreatePinterestBoardParams(String name) {
        body.put("name", name);
    }

    public static CreatePinterestBoardParams of(String name) {
        return new CreatePinterestBoardParams(name);
    }

    public CreatePinterestBoardParams description(String description) {
        body.put("description", description);
        return this;
    }

    public CreatePinterestBoardParams privacy(String privacy) {
        body.put("privacy", privacy);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
