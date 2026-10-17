package com.fopost.sdk.model;


/** The pagination block a contacts listing returns, beside its data. */
public record ContactPageMeta(Integer page, Integer perPage, Integer total) {}
