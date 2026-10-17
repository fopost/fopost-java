package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fopost.sdk.FoPostException;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.internal.Json;
import com.fopost.sdk.model.BlogArticle;
import com.fopost.sdk.model.BlogArticleFull;
import com.fopost.sdk.model.BlogKeyword;
import com.fopost.sdk.model.BlogSettings;
import com.fopost.sdk.model.BlogSettingsUpdate;
import com.fopost.sdk.model.BlogWebsiteTest;
import com.fopost.sdk.param.BlogParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Articles for a website: the keyword plan, the drafts the writer produces, and where they are
 * published. A post goes to a social network; an article goes to a website.
 *
 * <p>Reads, settings, keyword edits and {@code submitArticle} need the {@code posts} scope.
 * {@code suggestSeeds}, {@code planKeywords}, {@code requestArticle} and {@code regenerateArticle}
 * spend AI credits and need {@code ai}; an empty balance answers 402. {@code publishArticle} needs
 * {@code publish}, answers 409 for a draft that was never submitted, and 409 {@code no_target}
 * until the settings name a target. Every call names the workspace: on the query
 * string for a read, an update or a delete, in the body for a create.
 *
 * <pre>{@code
 * BlogArticle article = fopost.blog().requestArticle(
 *         workspace.id(), BlogParams.Article.create().keyword("how to schedule posts"));
 *
 * BlogArticleFull full = fopost.blog().getArticle(workspace.id(), article.id());
 * fopost.blog().submitArticle(workspace.id(), article.id());
 * fopost.blog().publishArticle(workspace.id(), article.id());
 * }</pre>
 */
public final class BlogResource {

    private final ApiClient http;

    public BlogResource(ApiClient http) {
        this.http = http;
    }

    // ── Settings ──

    public BlogSettings getSettings(String workspaceId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/blog/settings", workspace(workspaceId))), BlogSettings.class);
    }

    /**
     * Change the settings. Setting {@code websiteUrl} for the first time issues the signing secret
     * for that site; it comes back once on {@link BlogSettingsUpdate#websiteSecret()} and is never
     * shown again. Rotate it with {@link #rotateWebsiteSecret}.
     */
    public BlogSettingsUpdate updateSettings(String workspaceId, BlogParams.Settings params) {
        JsonNode body = http.patch("/v1/blog/settings", withWorkspace(workspaceId, params.toNode()));
        BlogSettings settings = http.convert(ApiClient.unwrap(body), BlogSettings.class);
        JsonNode secret = body.path("websiteSecret");
        return new BlogSettingsUpdate(settings, secret.isTextual() ? secret.asText() : null);
    }

    /** Issue a new website signing secret. The old one stops verifying at once. */
    public String rotateWebsiteSecret(String workspaceId) {
        JsonNode body = ApiClient.unwrap(http.post("/v1/blog/settings/website-secret", workspace(workspaceId)));
        return body.path("websiteSecret").asText(null);
    }

    /**
     * Send a signed {@code article.test} delivery to the website target. A site that does not
     * answer 2xx comes back with {@code ok} false and its status, rather than as an exception.
     */
    public BlogWebsiteTest testWebsite(String workspaceId) {
        try {
            return http.convert(
                    ApiClient.unwrap(http.post("/v1/blog/settings/website-test", workspace(workspaceId))),
                    BlogWebsiteTest.class);
        } catch (FoPostException e) {
            // The API answers 502 with the same {data: {ok, status, error}} body when the site fails.
            if (e.status() == 502 && e.body() instanceof JsonNode body && body.has("data")) {
                return http.convert(body.get("data"), BlogWebsiteTest.class);
            }
            throw e;
        }
    }

    // ── Keywords ──

    public List<BlogKeyword> listKeywords(String workspaceId) {
        return listKeywords(workspaceId, BlogParams.Filter.create());
    }

    public List<BlogKeyword> listKeywords(String workspaceId, BlogParams.Filter filter) {
        Map<String, Object> query = workspace(workspaceId);
        query.putAll(filter.toQuery());
        return http.convertList(ApiClient.unwrap(http.get("/v1/blog/keywords", query)), BlogKeyword.class);
    }

    /** About 10 search terms from the blog goal and topics. Spends AI credits. */
    public List<String> suggestSeeds(String workspaceId) {
        return suggestSeeds(workspaceId, List.of(), null);
    }

    /**
     * About 10 search terms from the blog goal and topics, to edit before {@link #planKeywords}.
     * Pass the current {@code seeds} (at most 20) to refine them and a {@code prompt} (up to 500
     * characters, or null) to steer. Spends AI credits.
     */
    public List<String> suggestSeeds(String workspaceId, List<String> seeds, String prompt) {
        Map<String, Object> body = workspace(workspaceId);
        if (!seeds.isEmpty()) {
            body.put("seeds", seeds);
        }
        if (prompt != null) {
            body.put("prompt", prompt);
        }
        JsonNode data = ApiClient.unwrap(http.post("/v1/blog/keywords/suggest", body));
        return http.convertList(data.path("seeds"), String.class);
    }

    /**
     * Queue a research run with terms derived from the blog settings. Results land as
     * {@code suggested} keywords; follow the run in {@link BlogSettings#planRun()}. Spends AI
     * credits.
     */
    public void planKeywords(String workspaceId) {
        planKeywords(workspaceId, List.of());
    }

    /**
     * Queue a research run around these seed terms. Results land as {@code suggested} keywords and
     * never fill the calendar; set one to {@code planned} with {@link #updateKeyword} to pick it.
     * Spends AI credits.
     */
    public void planKeywords(String workspaceId, List<String> seeds) {
        Map<String, Object> body = workspace(workspaceId);
        if (!seeds.isEmpty()) {
            body.put("seeds", seeds);
        }
        http.post("/v1/blog/keywords/plan", body);
    }

    public BlogKeyword addKeyword(String workspaceId, BlogParams.Keyword params) {
        Map<String, Object> body = workspace(workspaceId);
        body.putAll(params.toMap());
        return http.convert(ApiClient.unwrap(http.post("/v1/blog/keywords", body)), BlogKeyword.class);
    }

    public BlogKeyword updateKeyword(String workspaceId, String keywordId, BlogParams.KeywordUpdate params) {
        return http.convert(
                ApiClient.unwrap(http.request(
                        "PATCH", "/v1/blog/keywords/" + keywordId, params.toNode(), workspace(workspaceId))),
                BlogKeyword.class);
    }

    public void deleteKeyword(String workspaceId, String keywordId) {
        http.request("DELETE", "/v1/blog/keywords/" + keywordId, null, workspace(workspaceId));
    }

    // ── Articles ──

    public List<BlogArticle> listArticles(String workspaceId) {
        return listArticles(workspaceId, BlogParams.Filter.create());
    }

    public List<BlogArticle> listArticles(String workspaceId, BlogParams.Filter filter) {
        Map<String, Object> query = workspace(workspaceId);
        query.putAll(filter.toQuery());
        return http.convertList(ApiClient.unwrap(http.get("/v1/blog/articles", query)), BlogArticle.class);
    }

    /**
     * Ask the writer for an article. The answer is the queued row; poll {@link #getArticle} until
     * its status leaves {@code queued} and {@code writing}. Spends AI credits.
     */
    public BlogArticle requestArticle(String workspaceId, BlogParams.Article params) {
        Map<String, Object> body = workspace(workspaceId);
        body.putAll(params.toMap());
        return http.convert(ApiClient.unwrap(http.post("/v1/blog/articles", body)), BlogArticle.class);
    }

    public BlogArticleFull getArticle(String workspaceId, String articleId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/blog/articles/" + articleId, workspace(workspaceId))),
                BlogArticleFull.class);
    }

    /**
     * Edit an article. One that is not editable answers 409 {@code invalid_state}, as does a
     * {@code publishAt} on a draft that was never submitted with {@link #submitArticle}.
     */
    public BlogArticleFull updateArticle(String workspaceId, String articleId, BlogParams.ArticleUpdate params) {
        return http.convert(
                ApiClient.unwrap(http.request(
                        "PATCH", "/v1/blog/articles/" + articleId, params.toNode(), workspace(workspaceId))),
                BlogArticleFull.class);
    }

    /** Write the article again from its instructions. Spends AI credits. */
    public BlogArticle regenerateArticle(String workspaceId, String articleId) {
        return action(workspaceId, articleId, "regenerate");
    }

    /**
     * Publish an article in review to the configured target. Needs {@code publish}. A draft answers
     * 409 until it is submitted with {@link #submitArticle}; 409 {@code no_target} means the
     * settings name none.
     */
    public BlogArticle publishArticle(String workspaceId, String articleId) {
        return action(workspaceId, articleId, "publish");
    }

    /** Move a draft, failed or rejected article to {@code in_review}. Any other state answers 409. */
    public BlogArticle submitArticle(String workspaceId, String articleId) {
        return action(workspaceId, articleId, "submit");
    }

    /** Take an article out of review without publishing it. */
    public BlogArticle rejectArticle(String workspaceId, String articleId) {
        return action(workspaceId, articleId, "reject");
    }

    public void deleteArticle(String workspaceId, String articleId) {
        http.request("DELETE", "/v1/blog/articles/" + articleId, null, workspace(workspaceId));
    }

    private BlogArticle action(String workspaceId, String articleId, String action) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/blog/articles/" + articleId + "/" + action, null, workspace(workspaceId))),
                BlogArticle.class);
    }

    private static Map<String, Object> workspace(String workspaceId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("workspaceId", workspaceId);
        return map;
    }

    private static ObjectNode withWorkspace(String workspaceId, ObjectNode fields) {
        ObjectNode body = Json.MAPPER.createObjectNode();
        body.put("workspaceId", workspaceId);
        body.setAll(fields);
        return body;
    }
}
