package com.fopost.sdk.model;

import java.util.List;

/**
 * What the network delivers to the FoPost webhook for one account. {@code subscribed} is false
 * when the subscription lapsed or a required field is missing.
 */
public record WebhookSubscription(boolean subscribed, List<String> fields, List<String> missingFields) {}
