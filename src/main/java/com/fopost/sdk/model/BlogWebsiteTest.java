package com.fopost.sdk.model;

/** The outcome of a test delivery to the website target. {@code status} is the site's HTTP answer. */
public record BlogWebsiteTest(boolean ok, Integer status, String error) {}
