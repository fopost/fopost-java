package com.fopost.sdk.param;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A partial update to a post. Only the fields you set are sent, so everything else is left alone.
 *
 * <p>Setting content or accounts replaces the whole list.
 */
public final class UpdatePostParams {

    private final Map<String, Object> body = new LinkedHashMap<>();
    private final List<Map<String, Object>> content = new ArrayList<>();
    private boolean contentSet;

    public static UpdatePostParams create() {
        return new UpdatePostParams();
    }

    public UpdatePostParams accounts(List<String> accountIds) {
        body.put("accounts", accountIds == null ? List.of() : accountIds);
        return this;
    }

    public UpdatePostParams accounts(String... accountIds) {
        return accounts(List.of(accountIds));
    }

    public UpdatePostParams content(String text) {
        return block(ContentBlockInput.text(text));
    }

    public UpdatePostParams block(ContentBlockInput block) {
        if (block != null) {
            content.add(block.toMap());
            contentSet = true;
        }
        return this;
    }

    public UpdatePostParams blocks(List<ContentBlockInput> blocks) {
        if (blocks != null) {
            blocks.forEach(this::block);
        }
        return this;
    }

    public UpdatePostParams status(String status) {
        body.put("status", status);
        return this;
    }

    public UpdatePostParams scheduleAt(Instant scheduleAt) {
        body.put("schedule_at", Params.iso(scheduleAt));
        return this;
    }

    public UpdatePostParams contentType(String contentType) {
        body.put("content_type", contentType);
        return this;
    }

    public UpdatePostParams artifactType(String artifactType) {
        body.put("artifact_type", artifactType);
        return this;
    }

    public UpdatePostParams labels(List<String> labelIds) {
        body.put("labels", labelIds == null ? List.of() : labelIds);
        return this;
    }

    public UpdatePostParams title(String title) {
        body.put("title", title);
        return this;
    }

    public UpdatePostParams internalTitle(String internalTitle) {
        body.put("internal_title", internalTitle);
        return this;
    }

    public UpdatePostParams summary(String summary) {
        body.put("summary", summary);
        return this;
    }

    public UpdatePostParams autoPlug(boolean enabled, String content) {
        body.put("auto_plug", enabled);
        body.put("auto_plug_content", content);
        return this;
    }

    public UpdatePostParams repeat(int times, int gap, String unit) {
        body.put("repeatable", true);
        body.put("repeatable_times", times);
        body.put("repeatable_gap", gap);
        body.put("repeatable_gap_unit", unit);
        return this;
    }

    public UpdatePostParams settings(Map<String, Object> settings) {
        body.put("settings", settings);
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> out = new LinkedHashMap<>(body);
        if (contentSet) {
            out.put("content", content);
        }
        return out;
    }
}
