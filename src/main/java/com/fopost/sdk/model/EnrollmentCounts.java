package com.fopost.sdk.model;

/** Where a sequence's enrollments stand, by status. */
public record EnrollmentCounts(int total, int active, int completed, int stopped, int failed) {}
