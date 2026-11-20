package com.fopost.sdk.model;

import java.util.List;

/**
 * Alt text generated for an image, now saved on the media item.
 *
 * @param altTextSource {@code ai} for generated text
 * @param tags subject tags for searching the library
 */
public record AltText(String altText, String altTextSource, List<String> tags) {}
