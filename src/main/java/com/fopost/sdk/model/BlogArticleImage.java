package com.fopost.sdk.model;

/**
 * An image on an article. {@code position} is the index of the outline heading the image sits
 * under, and is absent on the featured image. A URL in a webhook payload is fetchable for six
 * hours; download and store your own copy.
 */
public record BlogArticleImage(String mediaId, String url, String alt, Integer position) {}
