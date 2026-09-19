package com.fopost.sdk.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Who a boost or ad is shown to.
 *
 * <p>At least one country or one location is required. Ids and names for the optional lists
 * come from {@code ads().searchTargeting(...)} and {@code ads().audiences(...)}.
 */
public final class AdTargetingParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private AdTargetingParams() {}

    /** {@code countries} are ISO 3166-1 alpha-2; {@code gender} is all, male or female. */
    public static AdTargetingParams create(List<String> countries, int ageMin, int ageMax, String gender) {
        AdTargetingParams params = new AdTargetingParams();
        params.body.put("countries", List.copyOf(countries));
        params.body.put("ageMin", ageMin);
        params.body.put("ageMax", ageMax);
        params.body.put("gender", gender);
        return params;
    }

    public AdTargetingParams audienceIds(List<String> audienceIds) {
        body.put("audienceIds", List.copyOf(audienceIds));
        return this;
    }

    /** {@code type} is region, city, zip or geo_market. */
    public AdTargetingParams location(String key, String name, String type) {
        Map<String, Object> location = new LinkedHashMap<>();
        location.put("key", key);
        location.put("name", name);
        location.put("type", type);
        append("locations", location);
        return this;
    }

    public AdTargetingParams interest(String id, String name) {
        append("interests", item(id, name));
        return this;
    }

    public AdTargetingParams behavior(String id, String name) {
        append("behaviors", item(id, name));
        return this;
    }

    public AdTargetingParams income(String id, String name) {
        append("income", item(id, name));
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }

    private static Map<String, Object> item(String id, String name) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("name", name);
        return item;
    }

    @SuppressWarnings("unchecked")
    private void append(String key, Map<String, Object> entry) {
        List<Map<String, Object>> list =
                (List<Map<String, Object>>) body.computeIfAbsent(key, ignored -> new ArrayList<>());
        list.add(entry);
    }
}
