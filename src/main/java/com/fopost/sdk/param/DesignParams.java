package com.fopost.sdk.param;

import com.fopost.sdk.internal.Json;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Filters and bodies for {@code designs()}.
 *
 * <p>A design document travels exactly as given: its keys and nulls are not renamed or dropped.
 *
 * <pre>{@code
 * DesignParams.Create.of(workspaceId, "post", document).name("Launch card");
 * DesignParams.Update.create().document(document).createVersion(true);
 * DesignParams.Export.of("png").pageIndexes(List.of(0, 1));
 * }</pre>
 */
public final class DesignParams {

    private DesignParams() {}

    /** Filters for the designs listing. */
    public static final class Filter {

        private final Map<String, Object> query = new LinkedHashMap<>();

        public static Filter of(String workspaceId) {
            Filter filter = new Filter();
            filter.query.put("workspace_id", workspaceId);
            return filter;
        }

        public Filter kind(String kind) {
            Params.put(query, "kind", kind);
            return this;
        }

        /** Defaults to 50, at most 100. */
        public Filter limit(int limit) {
            Params.put(query, "limit", limit);
            return this;
        }

        public Filter offset(int offset) {
            Params.put(query, "offset", offset);
            return this;
        }

        public Map<String, Object> toQuery() {
            return new LinkedHashMap<>(query);
        }
    }

    /** The body of a design create. */
    public static final class Create {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static Create of(String workspaceId, String kind, Map<String, Object> document) {
            Create create = new Create();
            create.body.put("workspace_id", workspaceId);
            create.body.put("kind", kind);
            create.body.put("document", Json.verbatim(document));
            return create;
        }

        /** Defaults to {@code Untitled Design}. */
        public Create name(String name) {
            Params.put(body, "name", name);
            return this;
        }

        public Create brandVoiceId(String brandVoiceId) {
            Params.put(body, "brand_voice_id", brandVoiceId);
            return this;
        }

        public Map<String, Object> toMap() {
            return new LinkedHashMap<>(body);
        }
    }

    /** A partial design update: only what was set is sent. */
    public static final class Update {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static Update create() {
            return new Update();
        }

        public Update name(String name) {
            Params.put(body, "name", name);
            return this;
        }

        public Update kind(String kind) {
            Params.put(body, "kind", kind);
            return this;
        }

        public Update document(Map<String, Object> document) {
            body.put("document", Json.verbatim(document));
            return this;
        }

        /** True writes a version row now; otherwise only every few saves do. */
        public Update createVersion(boolean createVersion) {
            body.put("create_version", createVersion);
            return this;
        }

        public Map<String, Object> toMap() {
            return new LinkedHashMap<>(body);
        }
    }

    /** What to render a design to. */
    public static final class Export {

        private final Map<String, Object> body = new LinkedHashMap<>();

        /** {@code png}, {@code jpg}, {@code pdf}, {@code mp4} or {@code gif}. */
        public static Export of(String format) {
            Export export = new Export();
            export.body.put("format", format);
            return export;
        }

        /** Zero-based pages to render; every page when not set. */
        public Export pageIndexes(List<Integer> pageIndexes) {
            Params.put(body, "page_indexes", pageIndexes);
            return this;
        }

        /** 5, 10 or 15 seconds, for {@code mp4} and {@code gif}. */
        public Export durationSec(int durationSec) {
            body.put("duration_sec", durationSec);
            return this;
        }

        public Map<String, Object> toMap() {
            return new LinkedHashMap<>(body);
        }
    }

    /** Text to split into carousel slides. Needs {@code text} or {@code postId}. */
    public static final class TextToSlides {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static TextToSlides of(String workspaceId) {
            TextToSlides params = new TextToSlides();
            params.body.put("workspace_id", workspaceId);
            return params;
        }

        public TextToSlides text(String text) {
            Params.put(body, "text", text);
            return this;
        }

        /** Read the text from a post in the workspace instead. */
        public TextToSlides postId(String postId) {
            Params.put(body, "post_id", postId);
            return this;
        }

        public TextToSlides maxSlides(int maxSlides) {
            body.put("max_slides", maxSlides);
            return this;
        }

        public TextToSlides brandVoiceId(String brandVoiceId) {
            Params.put(body, "brand_voice_id", brandVoiceId);
            return this;
        }

        public Map<String, Object> toMap() {
            return new LinkedHashMap<>(body);
        }
    }

    /** A text post or carousel built from post text. Needs {@code text} or {@code postId}. */
    public static final class FromPost {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static FromPost of(String workspaceId) {
            FromPost params = new FromPost();
            params.body.put("workspace_id", workspaceId);
            return params;
        }

        public FromPost text(String text) {
            Params.put(body, "text", text);
            return this;
        }

        public FromPost postId(String postId) {
            Params.put(body, "post_id", postId);
            return this;
        }

        /** Defaults to {@code quote}. */
        public FromPost style(String style) {
            Params.put(body, "style", style);
            return this;
        }

        /** A size preset id, for a single text post. */
        public FromPost preset(String preset) {
            Params.put(body, "preset", preset);
            return this;
        }

        /** The connected account whose name and handle the design shows. */
        public FromPost accountId(String accountId) {
            Params.put(body, "account_id", accountId);
            return this;
        }

        /** Unset, a long text becomes a carousel and a short one a single post. */
        public FromPost carousel(boolean carousel) {
            body.put("carousel", carousel);
            return this;
        }

        public FromPost name(String name) {
            Params.put(body, "name", name);
            return this;
        }

        public FromPost brandVoiceId(String brandVoiceId) {
            Params.put(body, "brand_voice_id", brandVoiceId);
            return this;
        }

        public Map<String, Object> toMap() {
            return new LinkedHashMap<>(body);
        }
    }
}
