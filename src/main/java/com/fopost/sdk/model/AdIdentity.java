package com.fopost.sdk.model;

/**
 * The account an ad runs as. Meta calls it a Page, TikTok an identity; an
 * identity id is what every route calls a {@code pageId}. {@code type} is the
 * network's own identity kind, e.g. {@code CUSTOMIZED_USER}.
 */
public record AdIdentity(String id, String type, String name, String avatarUrl) {}
