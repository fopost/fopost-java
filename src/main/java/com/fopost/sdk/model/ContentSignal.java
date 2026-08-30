package com.fopost.sdk.model;

/** An advisory content signal from a preflight check. {@code level} is {@code info} or {@code warn}. */
public record ContentSignal(String level, String code, String message) {}
