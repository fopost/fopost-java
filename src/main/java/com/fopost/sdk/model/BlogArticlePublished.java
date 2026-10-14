package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * The {@code data} of an {@code article.published} webhook, and of the delivery a website target
 * receives. Image URLs are fetchable for six hours; download and store your own copy.
 */
public record BlogArticlePublished(
        String id,
        String title,
        String slug,
        String excerpt,
        String metaTitle,
        String metaDescription,
        String bodyMarkdown,
        String bodyHtml,
        BlogArticleImage featuredImage,
        List<BlogArticleImage> images,
        List<String> tags,
        String keyword,
        int wordCount,
        Instant publishedAt,
        String publishedUrl) {}
