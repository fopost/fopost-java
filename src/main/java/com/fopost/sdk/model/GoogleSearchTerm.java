package com.fopost.sdk.model;

/** What someone actually searched, with the metrics it earned. */
public record GoogleSearchTerm(String term, String adGroupId, String status, AdInsights metrics) {}
