package com.fopost.sdk.model;

import java.util.List;

/**
 * The switches TikTok enforces at publish time. They are set on the TikTok account itself, not in
 * FoPost, so a disabled one cannot be turned back on here.
 */
public record TikTokCreatorInfo(
        String username,
        String nickname,
        String avatarUrl,
        List<String> privacyLevelOptions,
        boolean commentDisabled,
        boolean duetDisabled,
        boolean stitchDisabled,
        Integer maxVideoPostDurationSec) {}
