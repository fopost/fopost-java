package com.fopost.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * How the network attributes a sale or a sign-up back to an ad set. {@code campaignIds} are the ad
 * sets this rule is attached to.
 */
public record ConversionRule(
        String id,
        String name,
        @JsonProperty("type") String conversionType,
        String attribution,
        Integer postClickWindowDays,
        Integer viewThroughWindowDays,
        Long valueMinor,
        String currency,
        Boolean enabled,
        String createdAt,
        List<String> campaignIds) {}
