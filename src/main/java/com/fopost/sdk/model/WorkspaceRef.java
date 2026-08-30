package com.fopost.sdk.model;

/** A short reference to a workspace, as embedded in another resource. */
public record WorkspaceRef(String id, String name, String slug, String type) {}
