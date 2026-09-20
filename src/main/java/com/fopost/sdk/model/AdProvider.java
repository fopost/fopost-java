package com.fopost.sdk.model;

import java.util.List;
import java.util.Map;

/**
 * An ad network from the API's registry. {@code configured} false cannot be connected yet.
 *
 * <p>{@code capabilities} says what the network supports — campaigns, audiences, conversions,
 * forecasts, adLibrary and so on. {@code targetingFacets} is what {@code searchTargeting} accepts
 * here, and {@code trackingMacros} what the network expands in a creative's tracking parameters.
 */
public record AdProvider(
        String id,
        String name,
        String logo,
        Boolean configured,
        List<String> connectMethods,
        Map<String, Boolean> capabilities,
        List<String> targetingFacets,
        List<AdTrackingMacro> trackingMacros) {}
