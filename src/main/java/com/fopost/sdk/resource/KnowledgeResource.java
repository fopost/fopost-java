package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.KnowledgeMatch;
import com.fopost.sdk.model.KnowledgeSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The workspace knowledge base: what the workspace has told FoPost about
 * itself.
 *
 * <p>A source is an FAQ, a note, a page on your own site, or a plain-text/CSV
 * item from the media library. Retrieval over these is what grounds a drafted
 * inbox reply in your own answers instead of an invented one. Needs the {@code
 * inbox} scope.
 */
public final class KnowledgeResource {

    private final ApiClient http;

    public KnowledgeResource(ApiClient http) {
        this.http = http;
    }

    public List<KnowledgeSource> list() {
        return list(null);
    }

    /** Every source in the workspace. Only a {@code ready} one is searched. */
    public List<KnowledgeSource> list(String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (workspaceId != null) {
            query.put("workspace_id", workspaceId);
        }
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/knowledge/sources", query)), KnowledgeSource.class);
    }

    /**
     * Adds a source and queues it for indexing, so it comes back {@code pending}.
     *
     * <p>{@code kind} is {@code faq}, {@code text}, {@code url} or {@code file}. An {@code faq} or
     * {@code text} source needs {@code content}, a {@code url} source needs {@code url}, and a
     * {@code file} source needs {@code mediaId} pointing at a plain-text or CSV item in the same
     * workspace. Pass null for whatever the kind does not use.
     */
    public KnowledgeSource create(
            String kind,
            String title,
            String content,
            String url,
            String mediaId,
            String brandVoiceId,
            String workspaceId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("kind", kind);
        body.put("title", title);
        putIfPresent(body, "content", content);
        putIfPresent(body, "url", url);
        putIfPresent(body, "media_id", mediaId);
        putIfPresent(body, "brand_voice_id", brandVoiceId);
        putIfPresent(body, "workspace_id", workspaceId);
        return http.convert(
                ApiClient.unwrap(http.post("/v1/knowledge/sources", body)), KnowledgeSource.class);
    }

    /** An FAQ or a note, whose text you type here. */
    public KnowledgeSource createText(String kind, String title, String content) {
        return create(kind, title, content, null, null, null, null);
    }

    /** A page on your own site, re-read whenever you sync it. */
    public KnowledgeSource createUrl(String title, String url) {
        return create("url", title, null, url, null, null, null);
    }

    /** A plain-text or CSV item already in the media library. */
    public KnowledgeSource createFile(String title, String mediaId) {
        return create("file", title, null, null, mediaId, null, null);
    }

    /**
     * Partial update: only the non-null fields are sent. Changing the content or the URL returns
     * the source to {@code pending} and re-indexes it.
     */
    public KnowledgeSource update(
            String sourceId, String title, String content, String url, String brandVoiceId) {
        Map<String, Object> body = new LinkedHashMap<>();
        putIfPresent(body, "title", title);
        putIfPresent(body, "content", content);
        putIfPresent(body, "url", url);
        putIfPresent(body, "brand_voice_id", brandVoiceId);
        return http.convert(
                ApiClient.unwrap(http.patch("/v1/knowledge/sources/" + sourceId, body)),
                KnowledgeSource.class);
    }

    /** Removes the source and every passage indexed from it. */
    public void delete(String sourceId) {
        http.delete("/v1/knowledge/sources/" + sourceId);
    }

    /**
     * Reads the source again — a {@code url} source is re-fetched. Returns once the re-index is
     * queued, not once it has finished.
     */
    public void sync(String sourceId) {
        http.post("/v1/knowledge/sources/" + sourceId + "/sync", new LinkedHashMap<String, Object>());
    }

    public List<KnowledgeMatch> search(String q) {
        return search(q, null, null, null);
    }

    /**
     * The passages closest to a question, best first. An empty list is the honest answer when
     * nothing stored answers it. {@code topK} defaults to 5 and caps at 20.
     */
    public List<KnowledgeMatch> search(
            String q, Integer topK, String brandVoiceId, String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("q", q);
        putIfPresent(query, "top_k", topK);
        putIfPresent(query, "brand_voice_id", brandVoiceId);
        putIfPresent(query, "workspace_id", workspaceId);
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/knowledge/search", query)), KnowledgeMatch.class);
    }

    private static void putIfPresent(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }
}
