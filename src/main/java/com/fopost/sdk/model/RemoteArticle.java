package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * An article that already lives on a connected site, addressed by the
 * platform's own id.
 *
 * <p>{@code status} is one of {@code published}, {@code draft}, {@code pending}
 * or {@code scheduled}.
 */
public record RemoteArticle(
        String id,
        String blogId,
        String title,
        String bodyHtml,
        String excerpt,
        String status,
        String authorName,
        List<String> tags,
        String imageUrl,
        String url,
        Instant publishedAt,
        Instant updatedAt) {}
