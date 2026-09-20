package com.fopost.sdk.model;

/** A tappable prompt shown before the first message; {@code question} is up to 80 characters. */
public record MetaIceBreaker(String question, String payload) {}
