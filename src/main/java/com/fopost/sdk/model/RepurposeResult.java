package com.fopost.sdk.model;

import java.util.Map;

/** An article turned into a post per platform, keyed by platform name. */
public record RepurposeResult(String url, String title, Map<String, String> posts, AiCredits credits) {}
