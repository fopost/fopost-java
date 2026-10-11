package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * An article as a listing shows it. {@code status} is one of {@link BlogArticleStatus};
 * {@code origin} is {@code plan}, {@code user} or {@code agent}. {@code reviewUntil} is when a
 * draft may auto-publish, {@code publishAt} where it sits on the calendar, and {@code error} the
 * reason the last write or publish failed. {@link com.fopost.sdk.resource.BlogResource#getArticle}
 * returns the body as well, as a {@link BlogArticleFull}.
 */
public record BlogArticle(
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
        Instant updatedAt) {}
