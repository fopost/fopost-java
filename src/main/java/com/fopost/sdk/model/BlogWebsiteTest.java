package com.fopost.sdk.model;

/**
 * The outcome of a test delivery to the website target. {@code httpStatus} is the site's HTTP
 * answer; {@code error} says why a failed test failed.
 */
public record BlogWebsiteTest(boolean ok, Integer httpStatus, String error) {}
