package com.fopost.sdk.model;

/** Pagination counters returned alongside a page of results. */
public record PageMeta(
        Integer currentPage,
        Integer perPage,
        Integer total,
        Integer lastPage,
        Integer from,
        Integer to) {}
