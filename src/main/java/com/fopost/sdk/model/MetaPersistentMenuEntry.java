package com.fopost.sdk.model;

import java.util.List;

/** One locale's menu; {@code default} is the fallback every language uses. */
public record MetaPersistentMenuEntry(
        String locale, List<MetaMenuItem> callToActions, Boolean composerInputDisabled) {

    /** The default-locale menu, the one every language falls back to. */
    public static MetaPersistentMenuEntry defaultLocale(List<MetaMenuItem> items) {
        return new MetaPersistentMenuEntry("default", items, null);
    }
}
