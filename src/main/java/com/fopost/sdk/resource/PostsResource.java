package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.internal.Json;
import com.fopost.sdk.internal.Multipart;
import com.fopost.sdk.FoPostException;
import com.fopost.sdk.model.BulkActionResult;
import com.fopost.sdk.model.BulkImportResult;
import com.fopost.sdk.model.BulkImportValidation;
import com.fopost.sdk.model.CancelResult;
import com.fopost.sdk.model.ContentBlock;
import com.fopost.sdk.model.Delivery;
import com.fopost.sdk.model.MediaItem;
import com.fopost.sdk.model.DuplicatedPost;
import com.fopost.sdk.model.Page;
import com.fopost.sdk.model.PageMeta;
import com.fopost.sdk.model.Post;
import com.fopost.sdk.model.PostAnalytics;
import com.fopost.sdk.model.PreflightResult;
import com.fopost.sdk.model.PublishResult;
import com.fopost.sdk.model.PublishRun;
import com.fopost.sdk.model.RetryResult;
import com.fopost.sdk.model.UploadLimits;
import com.fopost.sdk.model.UploadedMedia;
import com.fopost.sdk.param.CreatePostParams;
import com.fopost.sdk.param.FileInput;
import com.fopost.sdk.param.PostListParams;
import com.fopost.sdk.param.PublishParams;
import com.fopost.sdk.param.RetryParams;
import com.fopost.sdk.param.UpdatePostParams;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Create, schedule, publish, and inspect posts. */
public final class PostsResource {

    private static final int DEFAULT_PER_PAGE = 30;
    private static final long DEFAULT_MULTIPART_MAX_FILE_BYTES = 100L * 1024 * 1024;

    private final ApiClient http;
    private final MediaResource media;

    public PostsResource(ApiClient http) {
        this.http = http;
        this.media = new MediaResource(http);
    }

    // ─── Reading ──────────────────────────────────────────────────────────────

    public Page<Post> list() {
        return list(PostListParams.create());
    }

    /** One page of posts. The page iterates over its items directly. */
    public Page<Post> list(PostListParams params) {
        JsonNode body = http.get("/v1/posts", params.toQuery());
        List<Post> items = http.convertList(body.path("data"), Post.class);
        PageMeta meta = http.convert(body.path("meta"), PageMeta.class);
        return new Page<>(items, meta);
    }

    /**
     * Every matching post, fetching one page at a time as you read.
     *
     * <pre>{@code
     * for (Post post : client.posts().autoPaginate(PostListParams.create().workspaceId(id))) {
     *     System.out.println(post.id());
     * }
     * }</pre>
     */
    public Iterable<Post> autoPaginate(PostListParams params) {
        return () -> new PostIterator(params);
    }

    public Stream<Post> stream(PostListParams params) {
        Spliterator<Post> spliterator =
                Spliterators.spliteratorUnknownSize(new PostIterator(params), Spliterator.ORDERED);
        return StreamSupport.stream(spliterator, false);
    }

    public Post get(String postId) {
        return http.convert(ApiClient.unwrap(http.get("/v1/posts/" + postId, null)), Post.class);
    }

    // ─── Writing ──────────────────────────────────────────────────────────────

    public Post create(CreatePostParams params) {
        return create(params, List.of());
    }

    /**
     * Create a post with files attached to its first content block.
     *
     * <p>Files within the account's multipart ceiling ride along in one multipart request; larger
     * ones are uploaded directly to the library first. Either way they end up on the first block,
     * and an oversize file answers {@code 413} with code {@code file_too_large}.
     */
    public Post create(CreatePostParams params, List<FileInput> files) {
        Map<String, Object> body = params.toMap();
        if (files == null || files.isEmpty()) {
            return http.convert(ApiClient.unwrap(http.post("/v1/posts", body)), Post.class);
        }
        if (fitsMultipart(files, (String) body.get("workspace_id"))) {
            return http.convert(
                    ApiClient.unwrap(http.request("POST", "/v1/posts", withFiles(body, files), null)), Post.class);
        }
        List<Map<String, Object>> content = blocks(body.get("content"));
        appendMedia(content, uploadAll((String) body.get("workspace_id"), files));
        body.put("content", content);
        return http.convert(ApiClient.unwrap(http.post("/v1/posts", body)), Post.class);
    }

    public Post update(String postId, UpdatePostParams params) {
        return update(postId, params, List.of());
    }

    /**
     * Update a post and attach files to its first content block, by the same route rule as
     * {@link #create(CreatePostParams, List)}. Without new content the post is read first so the
     * files are appended to what is already there.
     */
    public Post update(String postId, UpdatePostParams params, List<FileInput> files) {
        Map<String, Object> body = params.toMap();
        if (files == null || files.isEmpty()) {
            return http.convert(ApiClient.unwrap(http.put("/v1/posts/" + postId, body)), Post.class);
        }
        if (fitsMultipart(files, null)) {
            return http.convert(
                    ApiClient.unwrap(http.request("PUT", "/v1/posts/" + postId, withFiles(body, files), null)),
                    Post.class);
        }
        Post existing = null;
        if (!body.containsKey("content")) {
            existing = get(postId);
        }
        List<Map<String, Object>> content =
                existing == null ? blocks(body.get("content")) : blocksOf(existing.content());
        appendMedia(content, uploadAll(existing == null ? null : existing.workspaceId(), files));
        body.put("content", content);
        return http.convert(ApiClient.unwrap(http.put("/v1/posts/" + postId, body)), Post.class);
    }

    public void delete(String postId) {
        http.delete("/v1/posts/" + postId);
    }

    /** Copy a post as a fresh draft, keeping its content, accounts, and labels. */
    public DuplicatedPost duplicate(String postId) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/posts/" + postId + "/duplicate", null)), DuplicatedPost.class);
    }

    // ─── Publishing ───────────────────────────────────────────────────────────

    /** Queue the post for immediate delivery to every account on it. */
    public PublishResult publish(String postId) {
        return publish(postId, PublishParams.create());
    }

    public PublishResult publish(String postId, PublishParams params) {
        JsonNode body = http.post("/v1/posts/" + postId + "/publish", params.toMap());
        return http.convert(ApiClient.unwrap(body), PublishResult.class);
    }

    /** Retry the deliveries that failed, leaving the ones that succeeded alone. */
    public RetryResult retry(String postId) {
        return retry(postId, RetryParams.create());
    }

    public RetryResult retry(String postId, RetryParams params) {
        JsonNode body = http.post("/v1/posts/" + postId + "/retry", params.toMap());
        return http.convert(ApiClient.unwrap(body), RetryResult.class);
    }

    /** Cancel the deliveries that have not gone out yet. */
    public CancelResult cancel(String postId) {
        return cancel(postId, List.of());
    }

    public CancelResult cancel(String postId, List<String> accountIds) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (accountIds != null && !accountIds.isEmpty()) {
            body.put("accountIds", accountIds);
        }
        return http.convert(ApiClient.unwrap(http.post("/v1/posts/" + postId + "/cancel", body)), CancelResult.class);
    }

    /** Per-account blockers and advisory content signals, without publishing anything. */
    public PreflightResult preflight(String postId) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/posts/" + postId + "/preflight", null)), PreflightResult.class);
    }

    public List<Delivery> deliveries(String postId) {
        return http.convertList(ApiClient.unwrap(http.get("/v1/posts/" + postId + "/deliveries", null)), Delivery.class);
    }

    /** Every publish attempt made for this post, newest first. */
    public List<PublishRun> publishRuns(String postId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/posts/" + postId + "/publish-runs", null)), PublishRun.class);
    }

    public PostAnalytics analytics(String postId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/posts/" + postId + "/analytics", null)), PostAnalytics.class);
    }

    // ─── Bulk actions ─────────────────────────────────────────────────────────

    /**
     * Move a selection's schedule by {@code offsetMinutes}, forwards or back.
     *
     * <p>One transaction: a selection containing a post the key cannot reach changes nothing.
     */
    public BulkActionResult bulkShift(String workspaceId, List<String> postIds, int offsetMinutes) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("action", "shift");
        body.put("workspace_id", workspaceId);
        body.put("post_ids", postIds);
        body.put("offset_minutes", offsetMinutes);
        return http.convert(http.post("/v1/posts/bulk", body), BulkActionResult.class);
    }

    /** {@code mode} is {@code replace} (the default), {@code add}, or {@code remove}. */
    public BulkActionResult bulkLabel(String workspaceId, List<String> postIds, List<String> labelIds, String mode) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("action", "label");
        body.put("workspace_id", workspaceId);
        body.put("post_ids", postIds);
        body.put("label_ids", labelIds);
        if (mode != null) {
            body.put("mode", mode);
        }
        return http.convert(http.post("/v1/posts/bulk", body), BulkActionResult.class);
    }

    public BulkActionResult bulkDelete(String workspaceId, List<String> postIds) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("action", "delete");
        body.put("workspace_id", workspaceId);
        body.put("post_ids", postIds);
        return http.convert(http.post("/v1/posts/bulk", body), BulkActionResult.class);
    }

    // ─── Bulk import ──────────────────────────────────────────────────────────

    /** Check a CSV without creating anything. Every row comes back with its errors, if any. */
    public BulkImportValidation validateImport(Path csv, String workspaceId) {
        return validateImport(csv.getFileName().toString(), read(csv), workspaceId);
    }

    public BulkImportValidation validateImport(String filename, byte[] csv, String workspaceId) {
        Multipart body = csvUpload(filename, csv, workspaceId);
        return http.convert(http.request("POST", "/v1/posts/bulk-import/validate", body, null),
                BulkImportValidation.class);
    }

    /** Create every valid row. Keep the returned batch id to roll the import back. */
    public BulkImportResult commitImport(Path csv, String workspaceId) {
        return commitImport(csv.getFileName().toString(), read(csv), workspaceId);
    }

    public BulkImportResult commitImport(String filename, byte[] csv, String workspaceId) {
        Multipart body = csvUpload(filename, csv, workspaceId);
        return http.convert(http.request("POST", "/v1/posts/bulk-import/commit", body, null), BulkImportResult.class);
    }

    /** Delete every post a committed batch created. */
    public void rollbackImport(String batchId) {
        http.delete("/v1/posts/bulk-import/" + batchId);
    }

    // ─── Files on posts ───────────────────────────────────────────────────────

    private boolean fitsMultipart(List<FileInput> files, String workspaceId) {
        long ceiling = DEFAULT_MULTIPART_MAX_FILE_BYTES;
        try {
            UploadLimits limits = media.limits(workspaceId);
            if (limits != null && limits.multipartMaxFileBytes() != null) {
                ceiling = limits.multipartMaxFileBytes();
            }
        } catch (FoPostException ignored) {
            // The server enforces the ceiling either way; the default only picks the route.
        }
        for (FileInput file : files) {
            if (file.size() > ceiling) {
                return false;
            }
        }
        return true;
    }

    private static Multipart withFiles(Map<String, Object> body, List<FileInput> files) {
        Multipart multipart;
        try {
            multipart = new Multipart().field("post", Json.MAPPER.writeValueAsString(body));
        } catch (IOException e) {
            throw new FoPostException("fopost: could not encode the request body", e);
        }
        for (FileInput file : files) {
            multipart.file("files", file.filename(), file.mimeType(), file.source(), file.size());
        }
        return multipart;
    }

    private List<MediaItem> uploadAll(String workspaceId, List<FileInput> files) {
        List<MediaItem> items = new ArrayList<>();
        for (FileInput file : files) {
            UploadedMedia uploaded = media.uploadDirect(workspaceId, file);
            items.add(new MediaItem(file.mediaType(), uploaded.name(), uploaded.url(), uploaded.size(), null, null));
        }
        return items;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> blocks(Object content) {
        List<Map<String, Object>> copy = new ArrayList<>();
        if (content instanceof List<?> list) {
            for (Object block : list) {
                copy.add(new LinkedHashMap<>((Map<String, Object>) block));
            }
        }
        return copy;
    }

    private static List<Map<String, Object>> blocksOf(List<ContentBlock> content) {
        List<Map<String, Object>> copy = new ArrayList<>();
        if (content == null) {
            return copy;
        }
        for (ContentBlock block : content) {
            Map<String, Object> encoded = new LinkedHashMap<>();
            encoded.put("text", block.text() == null ? "" : block.text());
            if (block.media() != null && !block.media().isEmpty()) {
                encoded.put("media", encode(block.media()));
            }
            copy.add(encoded);
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    private static void appendMedia(List<Map<String, Object>> content, List<MediaItem> items) {
        if (content.isEmpty()) {
            Map<String, Object> block = new LinkedHashMap<>();
            block.put("text", "");
            content.add(block);
        }
        Map<String, Object> first = content.get(0);
        List<Map<String, Object>> media = new ArrayList<>();
        if (first.get("media") instanceof List<?> existing) {
            media.addAll((List<Map<String, Object>>) existing);
        }
        media.addAll(encode(items));
        first.put("media", media);
    }

    private static List<Map<String, Object>> encode(List<MediaItem> items) {
        List<Map<String, Object>> encoded = new ArrayList<>();
        for (MediaItem item : items) {
            Map<String, Object> entry = new LinkedHashMap<>();
            put(entry, "type", item.type());
            put(entry, "name", item.name());
            put(entry, "url", item.url());
            put(entry, "size", item.size());
            put(entry, "alt", item.alt());
            put(entry, "thumbnail", item.thumbnail());
            encoded.add(entry);
        }
        return encoded;
    }

    private static void put(Map<String, Object> into, String key, Object value) {
        if (value != null) {
            into.put(key, value);
        }
    }

    private static Multipart csvUpload(String filename, byte[] csv, String workspaceId) {
        return new Multipart().file("file", filename, "text/csv", csv).field("workspace_id", workspaceId);
    }

    private static byte[] read(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Walks pages lazily, so a large selection never has to fit in memory at once. */
    private final class PostIterator implements Iterator<Post> {

        private final PostListParams params;
        private final int perPage;
        private Iterator<Post> current = List.<Post>of().iterator();
        private int page;
        private boolean exhausted;

        private PostIterator(PostListParams params) {
            this.params = params;
            this.perPage = params.perPage() == null ? DEFAULT_PER_PAGE : params.perPage();
            this.page = params.page() == null ? 1 : params.page();
        }

        @Override
        public boolean hasNext() {
            while (!current.hasNext() && !exhausted) {
                fetchNextPage();
            }
            return current.hasNext();
        }

        @Override
        public Post next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return current.next();
        }

        private void fetchNextPage() {
            Page<Post> fetched = list(params.withPage(page).perPage(perPage));
            List<Post> items = new ArrayList<>(fetched.data());
            current = items.iterator();

            Integer lastPage = fetched.meta() == null ? null : fetched.meta().lastPage();
            if (items.isEmpty()
                    || (lastPage != null && page >= lastPage)
                    || (lastPage == null && items.size() < perPage)) {
                exhausted = true;
            }
            page++;
        }
    }
}
