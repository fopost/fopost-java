package com.fopost.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * A public ad from the network's own library, never a connection's own data. {@code payer} is the
 * paying entity, where the network discloses one.
 */
public record AdLibraryAd(
        String id,
        String advertiserName,
        String advertiserUrl,
        String headline,
        String body,
        @JsonProperty("type") String adType,
        String thumbnailUrl,
        String firstImpressionAt,
        String lastImpressionAt,
        List<String> countries,
        String detailsUrl,
        String payer,
        String impressionsRange) {}
