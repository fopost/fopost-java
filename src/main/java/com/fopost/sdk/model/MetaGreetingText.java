package com.fopost.sdk.model;

/** One locale's greeting, up to 160 characters. */
public record MetaGreetingText(String locale, String text) {

    /** The default-locale greeting. */
    public static MetaGreetingText of(String text) {
        return new MetaGreetingText("default", text);
    }
}
