package com.fopost.sdk.model;

/** The cursor on an X search: pass {@code nextToken} back to read the next page, null at the end. */
public record XSearchPageMeta(String nextToken) {}
