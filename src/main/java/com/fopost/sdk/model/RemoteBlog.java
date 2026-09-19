package com.fopost.sdk.model;

/**
 * A blog on a connected site. {@code id} is the platform's own id, never a
 * FoPost id.
 *
 * <p>A Shopify store reports every blog it has; WordPress has one implicit blog
 * and reports it under the id {@code default}, so both answer the same shape.
 */
public record RemoteBlog(String id, String title, String handle, String url) {}
