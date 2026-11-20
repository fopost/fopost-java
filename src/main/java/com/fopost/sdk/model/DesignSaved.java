package com.fopost.sdk.model;

/**
 * The result of an update or a version restore.
 *
 * @param version the version row this save wrote, or null when it did not write one
 */
public record DesignSaved(Design design, DesignVersion version) {}
