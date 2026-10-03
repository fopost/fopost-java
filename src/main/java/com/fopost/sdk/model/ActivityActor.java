package com.fopost.sdk.model;

/** Who did it: {@code user}, {@code api_key}, {@code agent} or {@code system}. */
public record ActivityActor(String type, String name) {}
