package com.fopost.sdk.model;

import java.util.List;

/** One Page an ads connection reaches, with its lead forms. */
public record LeadFormSource(
        String connectionId,
        String connectionName,
        String pageId,
        String pageName,
        List<LeadForm> forms,
        String error,
        String workspaceId) {}
