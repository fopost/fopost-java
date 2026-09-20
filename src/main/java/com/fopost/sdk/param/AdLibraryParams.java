package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** What an ad-library search narrows on. Dates are YYYY-MM-DD. */
public final class AdLibraryParams {

    private final Map<String, Object> query = new LinkedHashMap<>();

    private AdLibraryParams() {}

    public static AdLibraryParams on(String connectionId) {
        AdLibraryParams params = new AdLibraryParams();
        params.query.put("connection_id", connectionId);
        return params;
    }

    public AdLibraryParams workspaceId(String workspaceId) {
        query.put("workspace_id", workspaceId);
        return this;
    }

    public AdLibraryParams keyword(String keyword) {
        query.put("keyword", keyword);
        return this;
    }

    public AdLibraryParams advertiser(String advertiser) {
        query.put("advertiser", advertiser);
        return this;
    }

    /** ISO 3166-1 alpha-2 codes. */
    public AdLibraryParams countries(List<String> countries) {
        query.put("countries", String.join(",", countries));
        return this;
    }

    public AdLibraryParams since(String since) {
        query.put("since", since);
        return this;
    }

    public AdLibraryParams until(String until) {
        query.put("until", until);
        return this;
    }

    /** The {@code nextCursor} from the previous page. */
    public AdLibraryParams cursor(String cursor) {
        query.put("cursor", cursor);
        return this;
    }

    public Map<String, Object> toQuery() {
        return new LinkedHashMap<>(query);
    }
}
