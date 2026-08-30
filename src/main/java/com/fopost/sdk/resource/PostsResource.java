package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.internal.Multipart;
import com.fopost.sdk.model.BulkActionResult;
import com.fopost.sdk.model.BulkImportResult;
import com.fopost.sdk.model.BulkImportValidation;
import com.fopost.sdk.model.CancelResult;
import com.fopost.sdk.model.Delivery;
import com.fopost.sdk.model.DuplicatedPost;
import com.fopost.sdk.model.Page;
import com.fopost.sdk.model.PageMeta;
import com.fopost.sdk.model.Post;
import com.fopost.sdk.model.PostAnalytics;
import com.fopost.sdk.model.PreflightResult;
import com.fopost.sdk.model.PublishResult;
import com.fopost.sdk.model.PublishRun;
import com.fopost.sdk.model.RetryResult;
import com.fopost.sdk.param.CreatePostParams;
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

    private final ApiClient http;

    public PostsResource(ApiClient http) {
        this.http = http;
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
        return http.convert(ApiClient.unwrap(http.post("/v1/posts", params.toMap())), Post.class);
    }

    public Post update(String postId, UpdatePostParams params) {
        return http.convert(ApiClient.unwrap(http.put("/v1/posts/" + postId, params.toMap())), Post.class);
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
