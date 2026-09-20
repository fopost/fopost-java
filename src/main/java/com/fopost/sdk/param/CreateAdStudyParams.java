package com.fopost.sdk.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An A/B study splitting traffic evenly across two to five cells for the length of the flight.
 * Times are ISO 8601.
 */
public final class CreateAdStudyParams {

    private final Map<String, Object> body = new LinkedHashMap<>();
    private final List<Map<String, Object>> cells = new ArrayList<>();

    private CreateAdStudyParams() {}

    public static CreateAdStudyParams of(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String name,
            String startAt,
            String endAt) {
        CreateAdStudyParams params = new CreateAdStudyParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("name", name);
        params.body.put("startAt", startAt);
        params.body.put("endAt", endAt);
        return params;
    }

    /** One arm of the study: the campaigns it tests. */
    public CreateAdStudyParams cell(String name, List<String> objectIds) {
        Map<String, Object> cell = new LinkedHashMap<>();
        cell.put("name", name);
        cell.put("objectIds", objectIds);
        cells.add(cell);
        return this;
    }

    public CreateAdStudyParams description(String description) {
        body.put("description", description);
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> out = new LinkedHashMap<>(body);
        out.put("cells", new ArrayList<>(cells));
        return out;
    }
}
