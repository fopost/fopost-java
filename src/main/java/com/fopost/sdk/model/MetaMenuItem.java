package com.fopost.sdk.model;

/**
 * A persistent-menu item: a {@code postback} carrying {@code payload}, or a {@code web_url}
 * carrying an http(s) {@code url}. The unused field stays null and is not sent.
 */
public record MetaMenuItem(String type, String title, String payload, String url) {

    /** An item that sends {@code payload} to your webhook when tapped. */
    public static MetaMenuItem postback(String title, String payload) {
        return new MetaMenuItem("postback", title, payload, null);
    }

    /** An item that opens {@code url}. */
    public static MetaMenuItem link(String title, String url) {
        return new MetaMenuItem("web_url", title, null, url);
    }
}
