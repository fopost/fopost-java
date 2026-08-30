package com.fopost.sdk.model;

/** Credits charged by one AI call, and what is left afterwards. */
public record AiCredits(Integer charged, Integer remaining) {}
