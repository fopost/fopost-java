package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The connection and the Google Ads account a call runs against.
 *
 * <p>{@code customerId} is digits only and has to name an account the connection's grant reaches:
 * any other answers 404. The workspace may be left out on a read, and is required on a write.
 */
public final class GoogleAdsScope {

    private final String connectionId;
    private final String customerId;
    private String workspaceId;

    private GoogleAdsScope(String connectionId, String customerId) {
        this.connectionId = connectionId;
        this.customerId = customerId;
    }

    public static GoogleAdsScope of(String connectionId, String customerId) {
        return new GoogleAdsScope(connectionId, customerId);
    }

    /** A write names the workspace the connection lives in. */
    public GoogleAdsScope workspace(String workspaceId) {
        this.workspaceId = workspaceId;
        return this;
    }

    /** Snake_case, the way a read takes it on the query string. */
    public Map<String, Object> toQuery() {
        Map<String, Object> query = new LinkedHashMap<>();
        if (workspaceId != null) {
            query.put("workspace_id", workspaceId);
        }
        query.put("connection_id", connectionId);
        query.put("customer_id", customerId);
        return query;
    }

    /** CamelCase, the way a write takes it in the body. */
    public Map<String, Object> toMap() {
        Map<String, Object> body = new LinkedHashMap<>();
        if (workspaceId != null) {
            body.put("workspaceId", workspaceId);
        }
        body.put("connectionId", connectionId);
        body.put("customerId", customerId);
        return body;
    }
}
