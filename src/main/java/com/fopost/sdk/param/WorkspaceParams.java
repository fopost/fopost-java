package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fields of a workspace being created or updated.
 *
 * <p>{@code name} and {@code slug} are required on create; an update sends only what you set.
 */
public final class WorkspaceParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    public static WorkspaceParams create(String name, String slug) {
        WorkspaceParams params = new WorkspaceParams();
        params.body.put("name", name);
        params.body.put("slug", slug);
        return params;
    }

    /** An update with no required field. */
    public static WorkspaceParams update() {
        return new WorkspaceParams();
    }

    public WorkspaceParams name(String name) {
        body.put("name", name);
        return this;
    }

    public WorkspaceParams slug(String slug) {
        body.put("slug", slug);
        return this;
    }

    public WorkspaceParams type(String type) {
        body.put("type", type);
        return this;
    }

    public WorkspaceParams logo(String logo) {
        body.put("logo", logo);
        return this;
    }

    public WorkspaceParams website(String website) {
        body.put("website", website);
        return this;
    }

    /** An IANA zone, e.g. {@code Europe/Berlin}. Scheduled times are read against it. */
    public WorkspaceParams timezone(String timezone) {
        body.put("timezone", timezone);
        return this;
    }

    public WorkspaceParams country(String country) {
        body.put("country", country);
        return this;
    }

    public WorkspaceParams description(String description) {
        body.put("description", description);
        return this;
    }

    public WorkspaceParams language(String language) {
        body.put("language", language);
        return this;
    }

    /** Update only: hold every post for approval before it can be scheduled. */
    public WorkspaceParams requireApproval(boolean requireApproval) {
        body.put("requireApproval", requireApproval);
        return this;
    }

    /** Update only. */
    public WorkspaceParams aiAltTextEnabled(boolean enabled) {
        body.put("aiAltTextEnabled", enabled);
        return this;
    }

    /** Update only. */
    public WorkspaceParams brandColor(String brandColor) {
        body.put("brandColor", brandColor);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
