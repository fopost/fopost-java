package com.fopost.sdk.model;

import java.util.List;

/** The default post languages for a connection: up to three BCP-47 tags. */
public record BlueskyLanguages(List<String> languages) {}
