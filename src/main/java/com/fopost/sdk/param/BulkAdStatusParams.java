package com.fopost.sdk.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pause or resume up to 50 campaigns, ad sets and ads at once. {@code status} is active or paused. */
public final class BulkAdStatusParams {

    private final Map<String, Object> body = new LinkedHashMap<>();
    private final List<Map<String, Object>> objects = new ArrayList<>();

    private BulkAdStatusParams() {}

    public static BulkAdStatusParams of(String workspaceId, String connectionId, String status) {
        BulkAdStatusParams params = new BulkAdStatusParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("status", status);
        return params;
    }

    public BulkAdStatusParams campaign(String id) {
        return object(id, "campaign");
    }

    public BulkAdStatusParams adSet(String id) {
        return object(id, "ad_set");
    }

    public BulkAdStatusParams ad(String id) {
        return object(id, "ad");
    }

    /** {@code level} is campaign, ad_set or ad. */
    public BulkAdStatusParams object(String id, String level) {
        Map<String, Object> object = new LinkedHashMap<>();
        object.put("id", id);
        object.put("level", level);
        objects.add(object);
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>(body);
        map.put("objects", new ArrayList<>(objects));
        return map;
    }
}
