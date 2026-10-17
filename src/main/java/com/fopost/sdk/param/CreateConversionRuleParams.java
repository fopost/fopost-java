package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** A new conversion rule on one ad account. */
public final class CreateConversionRuleParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateConversionRuleParams() {}

    /**
     * {@code type} is purchase, lead, sign_up, add_to_cart, download, install, key_page_view or
     * other; {@code attribution} is last_touch or each_campaign.
     */
    public static CreateConversionRuleParams of(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String name,
            String type,
            String attribution) {
        CreateConversionRuleParams params = new CreateConversionRuleParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("name", name);
        params.body.put("type", type);
        params.body.put("attribution", attribution);
        return params;
    }

    public CreateConversionRuleParams postClickWindowDays(int days) {
        body.put("postClickWindowDays", days);
        return this;
    }

    public CreateConversionRuleParams viewThroughWindowDays(int days) {
        body.put("viewThroughWindowDays", days);
        return this;
    }

    /** What one conversion is worth, minor units. */
    public CreateConversionRuleParams value(long valueMinor, String currency) {
        body.put("valueMinor", valueMinor);
        body.put("currency", currency);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
