package com.fopost.sdk.model;

import java.util.List;

/** One rewritten variant per requested platform. */
public record RewriteResult(List<Variant> results, AiCredits credits) {

    public record Variant(String platform, String content, Integer credits) {}
}
