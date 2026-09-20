package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One row of a company-list upload. At least one of name, domain, page url or ticker is required;
 * the rows travel with the request and are never stored.
 */
public final class AdCompanyParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private AdCompanyParams() {}

    public static AdCompanyParams named(String name) {
        AdCompanyParams params = new AdCompanyParams();
        params.body.put("name", name);
        return params;
    }

    public static AdCompanyParams domain(String domain) {
        AdCompanyParams params = new AdCompanyParams();
        params.body.put("domain", domain);
        return params;
    }

    /** The company's page on the network. */
    public static AdCompanyParams pageUrl(String pageUrl) {
        AdCompanyParams params = new AdCompanyParams();
        params.body.put("pageUrl", pageUrl);
        return params;
    }

    public AdCompanyParams withName(String name) {
        body.put("name", name);
        return this;
    }

    public AdCompanyParams withDomain(String domain) {
        body.put("domain", domain);
        return this;
    }

    /** Stock ticker, where the network matches on one. */
    public AdCompanyParams withTicker(String ticker) {
        body.put("ticker", ticker);
        return this;
    }

    public AdCompanyParams withCountry(String country) {
        body.put("country", country);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
