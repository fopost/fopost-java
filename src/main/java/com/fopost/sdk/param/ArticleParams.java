package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The body of a create or update on an article.
 *
 * <p>Only what is set here travels, so on an update an omitted field keeps
 * whatever the site already had. A create needs at least {@code title} and
 * {@code body}; an update needs at least one field.
 */
public final class ArticleParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    public static ArticleParams create() {
        return new ArticleParams();
    }

    public ArticleParams title(String title) {
        body.put("title", title);
        return this;
    }

    /** FoPost body markup; the site's own format is rendered from it. */
    public ArticleParams body(String value) {
        body.put("body", value);
        return this;
    }

    public ArticleParams excerpt(String excerpt) {
        body.put("excerpt", excerpt);
        return this;
    }

    /** {@code published}, {@code draft}, {@code pending} or {@code scheduled}. */
    public ArticleParams status(String status) {
        body.put("status", status);
        return this;
    }

    public ArticleParams tags(List<String> tags) {
        body.put("tags", tags);
        return this;
    }

    public ArticleParams authorName(String authorName) {
        body.put("author_name", authorName);
        return this;
    }

    /** Public http(s) URL of the featured image. */
    public ArticleParams imageUrl(String imageUrl) {
        body.put("image_url", imageUrl);
        return this;
    }

    public Map<String, Object> toBody() {
        return new LinkedHashMap<>(body);
    }
}
