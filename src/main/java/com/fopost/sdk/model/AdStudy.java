package com.fopost.sdk.model;

/** An A/B study splitting traffic across its cells. */
public record AdStudy(
        String id,
        String name,
        String description,
        String type,
        String status,
        String startAt,
        String endAt) {}
