package com.fopost.sdk.model;

import java.util.List;

/** What a triggered analytics collection managed to fetch. */
public record CollectSummary(
        Integer accounts,
        Integer posts,
        Integer demographics,
        Integer errors,
        List<ErrorDetail> errorDetails) {

    public record ErrorDetail(
            String accountId,
            String platform,
            String username,
            String stage,
            String message) {}
}
