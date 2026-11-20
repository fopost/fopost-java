package com.fopost.sdk.model;

import java.util.List;

/**
 * Text split into carousel slides: a cover, the slides, a closing call to action, and the brand
 * the slides should wear.
 */
public record TextSlides(Cover cover, List<Slide> slides, Cta cta, Brand brand) {

    public record Cover(String title, String subtitle) {}

    public record Slide(String heading, String body) {}

    public record Cta(String heading, String body) {}

    public record Brand(List<String> colors, Fonts fonts, String logoMediaId) {}

    public record Fonts(String heading, String body) {}
}
