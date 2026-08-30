package com.fopost.sdk.model;

import java.util.List;

/** One block of a post. A single-block post is a plain update; several blocks make a thread. */
public record ContentBlock(String id, String text, List<MediaItem> media, Integer position) {}
