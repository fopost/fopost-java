package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines a custom conversion on a pixel. {@code eventType} is one of the network's standard
 * types ({@code PURCHASE}, {@code LEAD}, {@code OTHER}, ...). The rule reads {@code ruleEventName}
 * ({@code PageView} by default) narrowed by {@link #urlContains} or {@link #urlEquals}, one of
 * the two.
 */
public final class CreateCustomConversionParams {

    private final Map<String, Object> body = new LinkedHashMap<>();
    private final Map<String, Object> rule = new LinkedHashMap<>();

    private CreateCustomConversionParams() {}

    public static CreateCustomConversionParams of(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String pixelId,
            String name,
            String eventType) {
        CreateCustomConversionParams params = new CreateCustomConversionParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("pixelId", pixelId);
        params.body.put("name", name);
        params.body.put("eventType", eventType);
        return params;
    }

    public CreateCustomConversionParams ruleEventName(String eventName) {
        rule.put("eventName", eventName);
        return this;
    }

    public CreateCustomConversionParams urlContains(String value) {
        rule.put("urlContains", value);
        return this;
    }

    public CreateCustomConversionParams urlEquals(String url) {
        rule.put("urlEquals", url);
        return this;
    }

    public CreateCustomConversionParams description(String description) {
        body.put("description", description);
        return this;
    }

    /** Default value of one conversion, account currency, minor units. */
    public CreateCustomConversionParams defaultValueMinor(long defaultValueMinor) {
        body.put("defaultValueMinor", defaultValueMinor);
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> out = new LinkedHashMap<>(body);
        out.put("rule", new LinkedHashMap<>(rule));
        return out;
    }
}
