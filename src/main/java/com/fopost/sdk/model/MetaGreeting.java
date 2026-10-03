package com.fopost.sdk.model;

import java.util.List;

/** The greeting set on one account, one entry per locale. */
public record MetaGreeting(List<MetaGreetingText> greeting) {}
