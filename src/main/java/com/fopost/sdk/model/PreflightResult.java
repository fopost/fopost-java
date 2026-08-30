package com.fopost.sdk.model;

import java.util.List;
import java.util.Map;

/**
 * A preflight check: per-account blockers and advisory signals, without publishing anything.
 *
 * <p>{@code ready} is false when any account has a hard blocker in its {@code issues}.
 */
public record PreflightResult(Boolean ready, Map<String, Object> post, List<AccountCheck> accounts) {

    public record AccountCheck(
            String accountId,
            String platform,
            String username,
            Boolean ready,
            List<String> issues,
            Double score,
            List<ContentSignal> signals) {}

    public boolean isReady() {
        return Boolean.TRUE.equals(ready);
    }
}
