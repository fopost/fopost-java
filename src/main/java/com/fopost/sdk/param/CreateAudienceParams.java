package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A saved audience on an ad account: a customer list, a lookalike, or website visitors.
 *
 * <pre>{@code
 * CreateAudienceParams.customList(workspaceId, connectionId, "act_123", "Newsletter", emails);
 * CreateAudienceParams.lookalike(workspaceId, connectionId, "act_123", "Like buyers", originId, "US");
 * CreateAudienceParams.website(workspaceId, connectionId, "act_123", "Visitors", pixelId);
 * }</pre>
 */
public final class CreateAudienceParams {

    private final Map<String, Object> body = new LinkedHashMap<>();
    private final Map<String, Object> spec = new LinkedHashMap<>();

    private CreateAudienceParams(
            String workspaceId, String connectionId, String adAccountId, String name, String subtype) {
        body.put("workspaceId", workspaceId);
        body.put("connectionId", connectionId);
        body.put("adAccountId", adAccountId);
        body.put("name", name);
        spec.put("subtype", subtype);
    }

    /** {@code CUSTOM}: the emails are hashed before they leave the API. */
    public static CreateAudienceParams customList(
            String workspaceId, String connectionId, String adAccountId, String name, List<String> emails) {
        CreateAudienceParams params = new CreateAudienceParams(workspaceId, connectionId, adAccountId, name, "CUSTOM");
        params.spec.put("emails", List.copyOf(emails));
        return params;
    }

    /** {@code LOOKALIKE} of an existing audience in one country (ISO 3166-1 alpha-2). */
    public static CreateAudienceParams lookalike(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String name,
            String originAudienceId,
            String country) {
        CreateAudienceParams params =
                new CreateAudienceParams(workspaceId, connectionId, adAccountId, name, "LOOKALIKE");
        params.spec.put("originAudienceId", originAudienceId);
        params.spec.put("country", country);
        return params;
    }

    /** {@code WEBSITE}: visitors a pixel has seen. */
    public static CreateAudienceParams website(
            String workspaceId, String connectionId, String adAccountId, String name, String pixelId) {
        CreateAudienceParams params = new CreateAudienceParams(workspaceId, connectionId, adAccountId, name, "WEBSITE");
        params.spec.put("pixelId", pixelId);
        return params;
    }

    public CreateAudienceParams description(String description) {
        body.put("description", description);
        return this;
    }

    /** Lookalike only: 0.01 to 0.2 of the country's population. Defaults to 0.01. */
    public CreateAudienceParams ratio(double ratio) {
        spec.put("ratio", ratio);
        return this;
    }

    /** Website only: 1 to 180 days. Defaults to 30. */
    public CreateAudienceParams retentionDays(int retentionDays) {
        spec.put("retentionDays", retentionDays);
        return this;
    }

    /** Website only: keep visitors whose url contains this. */
    public CreateAudienceParams urlContains(String urlContains) {
        spec.put("urlContains", urlContains);
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>(body);
        map.put("spec", new LinkedHashMap<>(spec));
        return map;
    }
}
