package com.fopost.sdk.model;

import java.time.Instant;

/**
 * One thing the workspace has told FoPost about itself: an FAQ, a note, a page
 * on its own site, or a plain-text/CSV file from the media library.
 *
 * @param kind {@code faq}, {@code text}, {@code url} or {@code file}
 * @param status {@code pending}, {@code syncing}, {@code ready} or {@code failed};
 *     only a ready source is searched
 * @param statusMessage why the last sync failed, in plain words
 * @param url set for {@code url} sources
 * @param mediaId set for {@code file} sources: the media library item read
 * @param brandVoiceId null means the source serves the whole workspace
 * @param chunkCount searchable passages the last sync produced
 * @param content the typed text, for {@code faq} and {@code text} sources only
 */
public record KnowledgeSource(
        String id,
        String kind,
        String title,
        String status,
        String statusMessage,
        String url,
        String mediaId,
        String brandVoiceId,
        Integer chunkCount,
        String content,
        Instant lastSyncedAt,
        Instant createdAt,
        Instant updatedAt) {}
