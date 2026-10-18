package com.fopost.sdk.param;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fopost.sdk.internal.Json;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Request builders for {@link com.fopost.sdk.resource.BlogResource}. Bodies are camelCase on the
 * wire, and a field set to null is sent as null so the API clears it.
 */
public final class BlogParams {

    private BlogParams() {}

    // A plain Map would lose its nulls: the mapper is NON_NULL, and a cleared field must reach
    // the API as null.
    static ObjectNode node(Map<String, Object> values) {
        ObjectNode node = Json.MAPPER.createObjectNode();
        values.forEach((key, value) -> {
            if (value == null) {
                node.putNull(key);
            } else {
                node.set(key, Json.MAPPER.valueToTree(value));
            }
        });
        return node;
    }

    /** A partial update of the workspace's blog settings; only what is set is sent. */
    public static final class Settings {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static Settings create() {
            return new Settings();
        }

        /** The site the articles are written for; null clears it. */
        public Settings siteUrl(String siteUrl) {
            body.put("siteUrl", siteUrl);
            return this;
        }

        public Settings topic(String topic) {
            body.put("topic", topic);
            return this;
        }

        public Settings audience(String audience) {
            body.put("audience", audience);
            return this;
        }

        /** One of {@link com.fopost.sdk.model.BlogGoal}; null clears it. */
        public Settings goal(String goal) {
            body.put("goal", goal);
            return this;
        }

        /** What the blog wants to write about; null clears it. */
        public Settings focusTopics(String focusTopics) {
            body.put("focusTopics", focusTopics);
            return this;
        }

        /** What the blog never writes about; null clears it. */
        public Settings avoidTopics(String avoidTopics) {
            body.put("avoidTopics", avoidTopics);
            return this;
        }

        public Settings language(String language) {
            body.put("language", language);
            return this;
        }

        public Settings locationCode(int locationCode) {
            body.put("locationCode", locationCode);
            return this;
        }

        /** Articles planned per week, 0 to 7; 0 turns planning off. */
        public Settings cadencePerWeek(int cadencePerWeek) {
            body.put("cadencePerWeek", cadencePerWeek);
            return this;
        }

        /** Days before its planned date that a picked keyword starts being written, 0 to 14. */
        public Settings reviewHoldDays(int reviewHoldDays) {
            body.put("reviewHoldDays", reviewHoldDays);
            return this;
        }

        /** {@code draft}, {@code standard} or {@code best}. */
        public Settings imageTier(String imageTier) {
            body.put("imageTier", imageTier);
            return this;
        }

        /** Images placed in the body, 0 to 3. */
        public Settings inBodyImages(int inBodyImages) {
            body.put("inBodyImages", inBodyImages);
            return this;
        }

        public Settings minWords(int minWords) {
            body.put("minWords", minWords);
            return this;
        }

        public Settings maxWords(int maxWords) {
            body.put("maxWords", maxWords);
            return this;
        }

        /** The brand the writer takes its voice from; null returns to the workspace default. */
        public Settings brandVoiceId(String brandVoiceId) {
            body.put("brandVoiceId", brandVoiceId);
            return this;
        }

        /** One of {@link com.fopost.sdk.model.BlogTargetKind}; null leaves the blog with no target. */
        public Settings targetKind(String targetKind) {
            body.put("targetKind", targetKind);
            return this;
        }

        public Settings targetAccountId(String targetAccountId) {
            body.put("targetAccountId", targetAccountId);
            return this;
        }

        /**
         * Where published articles are POSTed as a signed webhook. Setting it for the first time
         * issues a secret, returned once on {@link com.fopost.sdk.model.BlogSettingsUpdate}.
         */
        public Settings websiteUrl(String websiteUrl) {
            body.put("websiteUrl", websiteUrl);
            return this;
        }

        public ObjectNode toNode() {
            return node(body);
        }
    }

    /** Filters for a keyword or article listing. */
    public static final class Filter {

        private final Map<String, Object> query = new LinkedHashMap<>();

        public static Filter create() {
            return new Filter();
        }

        /** One or more statuses; several are joined with a comma. */
        public Filter status(String... statuses) {
            query.put("status", String.join(",", statuses));
            return this;
        }

        public Filter limit(int limit) {
            query.put("limit", limit);
            return this;
        }

        public Map<String, Object> toQuery() {
            return new LinkedHashMap<>(query);
        }
    }

    /** A keyword added to the plan by hand. */
    public static final class Keyword {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static Keyword of(String keyword) {
            Keyword params = new Keyword();
            params.body.put("keyword", keyword);
            return params;
        }

        /** The day the article for it should be written. */
        public Keyword plannedFor(LocalDate plannedFor) {
            Params.put(body, "plannedFor", plannedFor == null ? null : plannedFor.toString());
            return this;
        }

        public Map<String, Object> toMap() {
            return new LinkedHashMap<>(body);
        }
    }

    /** Moves a keyword in the plan or re-dates it. */
    public static final class KeywordUpdate {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static KeywordUpdate create() {
            return new KeywordUpdate();
        }

        /**
         * One of {@link com.fopost.sdk.model.BlogKeywordStatus}. {@code planned} picks the keyword and
         * gives it the next free date.
         */
        public KeywordUpdate status(String status) {
            body.put("status", status);
            return this;
        }

        /** The day the article for it should be written; null takes it off the calendar. */
        public KeywordUpdate plannedFor(LocalDate plannedFor) {
            body.put("plannedFor", plannedFor == null ? null : plannedFor.toString());
            return this;
        }

        public ObjectNode toNode() {
            return node(body);
        }
    }

    /** Asks the writer for an article. One of {@code title}, {@code keyword} or {@code keywordId} is required. */
    public static final class Article {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static Article create() {
            return new Article();
        }

        public Article title(String title) {
            body.put("title", title);
            return this;
        }

        public Article keyword(String keyword) {
            body.put("keyword", keyword);
            return this;
        }

        /** A keyword from the plan; its term becomes the target. */
        public Article keywordId(String keywordId) {
            body.put("keywordId", keywordId);
            return this;
        }

        /** Up to 4000 characters of direction for the writer. */
        public Article instructions(String instructions) {
            body.put("instructions", instructions);
            return this;
        }

        public Map<String, Object> toMap() {
            return new LinkedHashMap<>(body);
        }
    }

    /** Edits an article. One still being written or already publishing answers 409. */
    public static final class ArticleUpdate {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static ArticleUpdate create() {
            return new ArticleUpdate();
        }

        public ArticleUpdate title(String title) {
            body.put("title", title);
            return this;
        }

        /** Lowercase letters, digits and hyphens. */
        public ArticleUpdate slug(String slug) {
            body.put("slug", slug);
            return this;
        }

        public ArticleUpdate excerpt(String excerpt) {
            body.put("excerpt", excerpt);
            return this;
        }

        public ArticleUpdate metaTitle(String metaTitle) {
            body.put("metaTitle", metaTitle);
            return this;
        }

        public ArticleUpdate metaDescription(String metaDescription) {
            body.put("metaDescription", metaDescription);
            return this;
        }

        public ArticleUpdate bodyMarkdown(String bodyMarkdown) {
            body.put("bodyMarkdown", bodyMarkdown);
            return this;
        }

        public ArticleUpdate tags(List<String> tags) {
            body.put("tags", tags);
            return this;
        }

        /**
         * Puts an article in review on the calendar; a draft answers 409 until it is submitted. Null
         * returns it to draft.
         */
        public ArticleUpdate publishAt(Instant publishAt) {
            body.put("publishAt", Params.iso(publishAt));
            return this;
        }

        public ObjectNode toNode() {
            return node(body);
        }
    }
}
