package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** Changes to a conversion rule. Only the fields you set move. */
public final class UpdateConversionRuleParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    public static UpdateConversionRuleParams create() {
        return new UpdateConversionRuleParams();
    }

    public UpdateConversionRuleParams name(String name) {
        body.put("name", name);
        return this;
    }

    public UpdateConversionRuleParams type(String type) {
        body.put("type", type);
        return this;
    }

    public UpdateConversionRuleParams attribution(String attribution) {
        body.put("attribution", attribution);
        return this;
    }

    public UpdateConversionRuleParams postClickWindowDays(int days) {
        body.put("postClickWindowDays", days);
        return this;
    }

    public UpdateConversionRuleParams viewThroughWindowDays(int days) {
        body.put("viewThroughWindowDays", days);
        return this;
    }

    public UpdateConversionRuleParams value(long valueMinor, String currency) {
        body.put("valueMinor", valueMinor);
        body.put("currency", currency);
        return this;
    }

    public UpdateConversionRuleParams enabled(boolean enabled) {
        body.put("enabled", enabled);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
