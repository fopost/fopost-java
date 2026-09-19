package com.fopost.sdk.model;

import java.util.List;

/**
 * A standalone post check: per-platform blockers and advisory signals, without creating a post.
 *
 * <p>{@code ready} is true only when every platform is ready.
 */
public record PostValidation(Boolean ready, List<PlatformCheck> platforms) {

    public record PlatformCheck(
            String platform, Boolean ready, List<String> issues, Double score, List<ContentSignal> signals) {}

    public boolean isReady() {
        return Boolean.TRUE.equals(ready);
    }
}
