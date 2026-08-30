package com.fopost.sdk.model;

import java.util.List;

/** The answer to a cancel: the deliveries that were pending and are now cancelled. */
public record CancelResult(String postStatus, List<Delivery> deliveries) {}
