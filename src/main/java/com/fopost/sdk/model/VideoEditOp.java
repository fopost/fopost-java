package com.fopost.sdk.model;

/**
 * A video edit step the API knows.
 *
 * @param available false when this deployment cannot run it right now
 * @param usesAi true when it spends AI, so an API key also needs the {@code ai} scope
 */
public record VideoEditOp(String name, boolean available, boolean usesAi) {}
