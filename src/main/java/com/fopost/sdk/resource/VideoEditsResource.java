package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.internal.Json;
import com.fopost.sdk.model.VideoEdit;
import com.fopost.sdk.model.VideoEditOp;
import com.fopost.sdk.model.VideoEditOperation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Video edits: trim, cut, subtitles, reframe and the other steps from {@link #ops()}, run as a job
 * over a video in the media library.
 *
 * <pre>{@code
 * VideoEdit job = client.videoEdits().create(mediaId, List.of(
 *         VideoEditOperation.of("trim", Map.of("startSec", 2, "endSec", 12)),
 *         VideoEditOperation.of("reframe", Map.of("aspect", "9:16"))));
 * // later
 * job = client.videoEdits().get(job.id());
 * }</pre>
 *
 * <p>Needs the {@code posts} scope and create access on the workspace. A step whose {@code usesAi}
 * is true also needs {@code ai}, and an empty balance answers {@code 402}. A job runs in the
 * background, so {@link #create} returns it {@code queued}; read it again with {@link #get}.
 */
public final class VideoEditsResource {

    private final ApiClient http;

    public VideoEditsResource(ApiClient http) {
        this.http = http;
    }

    /** Every step the API knows, and whether it can run now. */
    public List<VideoEditOp> ops() {
        return http.convertList(ApiClient.unwrap(http.get("/v1/video-edits/ops", null)), VideoEditOp.class);
    }

    /** The workspace's jobs, newest first. */
    public List<VideoEdit> list(String workspaceId) {
        return list(workspaceId, null, null);
    }

    /**
     * Needs {@code workspaceId} or {@code mediaId}; with only {@code mediaId}, the workspace is the
     * one owning that video. {@code limit} defaults to 20.
     */
    public List<VideoEdit> list(String workspaceId, String mediaId, Integer limit) {
        Map<String, Object> query = new LinkedHashMap<>();
        putIfPresent(query, "workspace_id", workspaceId);
        putIfPresent(query, "media_id", mediaId);
        putIfPresent(query, "limit", limit);
        return http.convertList(ApiClient.unwrap(http.get("/v1/video-edits", query)), VideoEdit.class);
    }

    /** Queues a job in the workspace that owns the video. At most 10 steps. */
    public VideoEdit create(String mediaId, List<VideoEditOperation> operations) {
        return create(null, mediaId, operations);
    }

    /** As above, naming the workspace; null resolves it from the video. */
    public VideoEdit create(String workspaceId, String mediaId, List<VideoEditOperation> operations) {
        Map<String, Object> body = new LinkedHashMap<>();
        putIfPresent(body, "workspace_id", workspaceId);
        body.put("media_id", mediaId);
        body.put("operations", Json.verbatim(operations));
        return http.convert(ApiClient.unwrap(http.post("/v1/video-edits", body)), VideoEdit.class);
    }

    public VideoEdit get(String jobId) {
        return get(jobId, null);
    }

    public VideoEdit get(String jobId, String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        putIfPresent(query, "workspace_id", workspaceId);
        return http.convert(ApiClient.unwrap(http.get("/v1/video-edits/" + jobId, query)), VideoEdit.class);
    }

    private static void putIfPresent(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }
}
