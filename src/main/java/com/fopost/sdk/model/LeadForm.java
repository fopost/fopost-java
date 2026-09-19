package com.fopost.sdk.model;

import java.util.List;

/** An instant form on a Page. */
public record LeadForm(
        String id, String name, String status, Integer leadsCount, String createdAt, List<String> questions) {}
