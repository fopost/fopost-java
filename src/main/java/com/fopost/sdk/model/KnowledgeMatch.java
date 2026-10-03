package com.fopost.sdk.model;

/**
 * One retrieved passage, with the source it came from so a reply can cite it.
 *
 * @param score similarity to the question, 0-1
 */
public record KnowledgeMatch(
        String sourceId,
        String sourceTitle,
        String sourceKind,
        String sourceUrl,
        String text,
        Double score) {}
