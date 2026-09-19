package com.fopost.sdk.model;

import java.util.List;

/** An instant form with its Page, privacy policy and locale. */
public record LeadFormDetail(
        String id,
        String name,
        String status,
        Integer leadsCount,
        String createdAt,
        List<String> questions,
        String pageId,
        String privacyPolicyUrl,
        String locale) {}
