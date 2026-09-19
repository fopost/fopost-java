package com.fopost.sdk.model;

import java.util.List;

/** The audiences and pixels on one ad account. */
public record AudiencesResult(List<Audience> audiences, List<Pixel> pixels, String workspaceId) {

    public record Pixel(String id, String name) {}
}
