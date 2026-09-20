package com.fopost.sdk.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Weights conversions so some audiences count for more than others: one to twenty rules. */
public final class CreateValueRuleSetParams {

    private final Map<String, Object> body = new LinkedHashMap<>();
    private final List<Map<String, Object>> rules = new ArrayList<>();

    private CreateValueRuleSetParams() {}

    public static CreateValueRuleSetParams of(
            String workspaceId, String connectionId, String adAccountId, String name) {
        CreateValueRuleSetParams params = new CreateValueRuleSetParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("name", name);
        return params;
    }

    public CreateValueRuleSetParams rule(String condition, double multiplier) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("condition", condition);
        row.put("multiplier", multiplier);
        rules.add(row);
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> out = new LinkedHashMap<>(body);
        out.put("rules", new ArrayList<>(rules));
        return out;
    }
}
