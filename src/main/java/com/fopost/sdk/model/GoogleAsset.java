package com.fopost.sdk.model;

/** A sitelink, callout, or structured snippet. */
public record GoogleAsset(String id, String name, String type, String text, String finalUrl) {}
