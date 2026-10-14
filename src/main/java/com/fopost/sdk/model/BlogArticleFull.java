package com.fopost.sdk.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.List;

/**
 * An article with its body: every field of {@link BlogArticle} plus the instructions it was
 * written from, the outline, the research brief, both renderings of the body and the in-body
 * images.
 */
public record BlogArticleFull(
        String id,
        String title,
        String slug,
        String keyword,
        String excerpt,
        String metaTitle,
        String metaDescription,
        BlogArticleImage featuredImage,
        List<String> tags,
        int wordCount,
        String status,
        String origin,
        Instant reviewUntil,
        Instant publishAt,
        Instant publishedAt,
        String publishedUrl,
        String error,
        Instant createdAt,
        Instant updatedAt,
        String instructions,
        List<String> outline,
        JsonNode brief,
        String bodyMarkdown,
        String bodyHtml,
        List<BlogArticleImage> images) {}
