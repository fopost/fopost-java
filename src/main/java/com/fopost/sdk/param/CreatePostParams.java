package com.fopost.sdk.param;

import com.fopost.sdk.model.PostStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A post to create.
 *
 * <pre>{@code
 * CreatePostParams.of(workspaceId)
 *     .accounts(accountId)
 *     .content("Hello from Java")
 *     .schedule(Instant.parse("2026-09-01T10:00:00Z"));
 * }</pre>
 *
 * <p>{@code status} is {@code draft} or {@code scheduled}, and a scheduled post needs a time.
 * To send something out now, create it and call {@code posts().publish(id)}.
 */
public final class CreatePostParams {

    private final String workspaceId;
    private final List<String> accounts = new ArrayList<>();
    private final List<Map<String, Object>> content = new ArrayList<>();
    private final List<String> labels = new ArrayList<>();
    private final List<String> sourceIds = new ArrayList<>();
    private String status = PostStatus.DRAFT;
    private String contentType;
    private String artifactType;
    private Instant scheduleAt;
    private Boolean repeatable;
    private Integer repeatableTimes;
    private Integer repeatableGap;
    private String repeatableGapUnit;
    private String title;
    private String internalTitle;
    private String summary;
    private Boolean autoPlug;
    private String autoPlugContent;
    private Map<String, Object> settings;
    private String companionOf;

    private CreatePostParams(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public static CreatePostParams of(String workspaceId) {
        return new CreatePostParams(workspaceId);
    }

    public CreatePostParams accounts(String... accountIds) {
        for (String id : accountIds) {
            if (id != null) {
                accounts.add(id);
            }
        }
        return this;
    }

    public CreatePostParams accounts(List<String> accountIds) {
        if (accountIds != null) {
            accounts.addAll(accountIds);
        }
        return this;
    }

    /** One block of text. Call again, or use {@link #block}, to build a thread. */
    public CreatePostParams content(String text) {
        return block(ContentBlockInput.text(text));
    }

    public CreatePostParams block(ContentBlockInput block) {
        if (block != null) {
            content.add(block.toMap());
        }
        return this;
    }

    public CreatePostParams blocks(List<ContentBlockInput> blocks) {
        if (blocks != null) {
            blocks.forEach(this::block);
        }
        return this;
    }

    public CreatePostParams status(String status) {
        this.status = status;
        return this;
    }

    /** Sets {@code status} to {@code scheduled} and pins the time. */
    public CreatePostParams schedule(Instant scheduleAt) {
        this.status = PostStatus.SCHEDULED;
        this.scheduleAt = scheduleAt;
        return this;
    }

    public CreatePostParams scheduleAt(Instant scheduleAt) {
        this.scheduleAt = scheduleAt;
        return this;
    }

    /** {@code post}, {@code thread} or {@code reel}. */
    public CreatePostParams contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public CreatePostParams artifactType(String artifactType) {
        this.artifactType = artifactType;
        return this;
    }

    public CreatePostParams labels(String... labelIds) {
        for (String id : labelIds) {
            if (id != null) {
                labels.add(id);
            }
        }
        return this;
    }

    public CreatePostParams labels(List<String> labelIds) {
        if (labelIds != null) {
            labels.addAll(labelIds);
        }
        return this;
    }

    /** Repeat the post {@code times} more, {@code gap} {@code unit} apart (hours, days, weeks, months). */
    public CreatePostParams repeat(int times, int gap, String unit) {
        this.repeatable = true;
        this.repeatableTimes = times;
        this.repeatableGap = gap;
        this.repeatableGapUnit = unit;
        return this;
    }

    public CreatePostParams title(String title) {
        this.title = title;
        return this;
    }

    public CreatePostParams internalTitle(String internalTitle) {
        this.internalTitle = internalTitle;
        return this;
    }

    public CreatePostParams summary(String summary) {
        this.summary = summary;
        return this;
    }

    /** Follow the post up with a second one, once it is live. */
    public CreatePostParams autoPlug(String content) {
        this.autoPlug = true;
        this.autoPlugContent = content;
        return this;
    }

    /** Per-platform overrides, keyed by platform name. */
    public CreatePostParams settings(Map<String, Object> settings) {
        this.settings = settings;
        return this;
    }

    public CreatePostParams sourceIds(List<String> sourceIds) {
        if (sourceIds != null) {
            this.sourceIds.addAll(sourceIds);
        }
        return this;
    }

    public CreatePostParams companionOf(String postId) {
        this.companionOf = postId;
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspace_id", workspaceId);
        body.put("accounts", accounts);
        body.put("content", content);
        Params.put(body, "status", status);
        Params.put(body, "content_type", contentType);
        Params.put(body, "artifact_type", artifactType);
        Params.put(body, "schedule_at", Params.iso(scheduleAt));
        Params.put(body, "repeatable", repeatable);
        Params.put(body, "repeatable_times", repeatableTimes);
        Params.put(body, "repeatable_gap", repeatableGap);
        Params.put(body, "repeatable_gap_unit", repeatableGapUnit);
        Params.put(body, "title", title);
        Params.put(body, "internal_title", internalTitle);
        Params.put(body, "summary", summary);
        Params.put(body, "auto_plug", autoPlug);
        Params.put(body, "auto_plug_content", autoPlugContent);
        Params.put(body, "settings", settings);
        Params.put(body, "companion_of", companionOf);
        if (!labels.isEmpty()) {
            body.put("labels", labels);
        }
        if (!sourceIds.isEmpty()) {
            body.put("source_ids", sourceIds);
        }
        return body;
    }
}
