package com.fopost.sdk.model;

/** One CSV row the import could not read. */
public record ContactImportSkip(int row, String reason) {}
