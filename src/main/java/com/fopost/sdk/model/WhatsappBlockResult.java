package com.fopost.sdk.model;

import java.util.List;

/** What the platform took and what it refused. */
public record WhatsappBlockResult(List<String> blocked, List<String> unblocked, List<String> failed) {}
