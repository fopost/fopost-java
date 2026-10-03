package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.ApiClient;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage a connected Google Business Profile location: the profile itself,
 * attributes, food menus, services, photos, place action links, verification
 * and performance.
 *
 * <p>Google grants Business Profile API access per project. Until that grant
 * lands on a deployment every call here fails with a 503 {@code
 * configuration_error}.
 *
 * <p>Responses relay Google's own shape, field for field, so they come back as
 * {@link JsonNode} rather than models we would have to keep chasing.
 */
public final class GoogleBusinessResource {

    /** The daily metrics fetched when a caller names none. */
    public static final List<String> DEFAULT_DAILY_METRICS = List.of(
            "BUSINESS_IMPRESSIONS_DESKTOP_MAPS",
            "BUSINESS_IMPRESSIONS_DESKTOP_SEARCH",
            "BUSINESS_IMPRESSIONS_MOBILE_MAPS",
            "BUSINESS_IMPRESSIONS_MOBILE_SEARCH",
            "CALL_CLICKS",
            "WEBSITE_CLICKS",
            "BUSINESS_DIRECTION_REQUESTS");

    private final ApiClient http;

    public GoogleBusinessResource(ApiClient http) {
        this.http = http;
    }

    /** The connected location, in the Business Information shape. */
    public JsonNode getLocation(String accountId) {
        return ApiClient.unwrap(http.get(path(accountId, "/location"), null));
    }

    /**
     * Patch the profile. Only the keys the map carries change; a null value
     * clears that field. Keys are the API's own snake_case names, such as
     * {@code title}, {@code description}, {@code website_uri} and
     * {@code primary_phone}.
     */
    public JsonNode updateLocation(String accountId, Map<String, Object> fields) {
        return ApiClient.unwrap(http.request("PATCH", path(accountId, "/location"), body(fields), null));
    }

    /** The attribute values set on the location. */
    public JsonNode getAttributes(String accountId) {
        return getAttributes(accountId, false, null, null, null);
    }

    /**
     * The attribute values set on the location, or, with {@code available},
     * the attributes Google offers for its category and region.
     */
    public JsonNode getAttributes(
            String accountId, boolean available, String categoryName, String regionCode, String languageCode) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (available) {
            query.put("available", "true");
        }
        putIfPresent(query, "category_name", categoryName);
        putIfPresent(query, "region_code", regionCode);
        putIfPresent(query, "language_code", languageCode);
        return ApiClient.unwrap(http.get(path(accountId, "/attributes"), query.isEmpty() ? null : query));
    }

    /** Only the named attributes change; every other one is left alone. */
    public JsonNode updateAttributes(String accountId, List<Map<String, Object>> attributes) {
        return ApiClient.unwrap(http.request(
                "PATCH", path(accountId, "/attributes"), Map.of("attributes", attributes), null));
    }

    /** The location's food menus. */
    public JsonNode getMenus(String accountId) {
        return ApiClient.unwrap(http.get(path(accountId, "/menus"), null));
    }

    /** Google has no per-section patch, so the whole menu set is replaced. */
    public JsonNode replaceMenus(String accountId, List<Map<String, Object>> menus) {
        return ApiClient.unwrap(http.put(path(accountId, "/menus"), Map.of("menus", menus)));
    }

    /** The location's service list. */
    public JsonNode getServices(String accountId) {
        return ApiClient.unwrap(http.get(path(accountId, "/services"), null));
    }

    /** Replace the whole service list. */
    public JsonNode replaceServices(String accountId, List<Map<String, Object>> serviceItems) {
        return ApiClient.unwrap(http.put(path(accountId, "/services"), Map.of("service_items", serviceItems)));
    }

    /** The photos on the location. */
    public JsonNode listMedia(String accountId) {
        return listMedia(accountId, 0, null);
    }

    public JsonNode listMedia(String accountId, int pageSize, String pageToken) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (pageSize > 0) {
            query.put("page_size", pageSize);
        }
        putIfPresent(query, "page_token", pageToken);
        return ApiClient.unwrap(http.get(path(accountId, "/media"), query.isEmpty() ? null : query));
    }

    /**
     * Add a photo from the media library. The asset has to be in a workspace
     * the caller can reach, and JPEG or PNG.
     */
    public JsonNode addMedia(String accountId, String mediaId, String category, String description) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("media_id", mediaId);
        body.put("category", category == null ? "ADDITIONAL" : category);
        putIfPresent(body, "description", description);
        return ApiClient.unwrap(http.post(path(accountId, "/media"), body));
    }

    /** Remove a photo by the media key Google returned. */
    public JsonNode deleteMedia(String accountId, String mediaKey) {
        return ApiClient.unwrap(http.delete(path(accountId, "/media/" + mediaKey)));
    }

    /** The Book, Order and Reserve links on the listing. */
    public JsonNode listPlaceActions(String accountId) {
        return ApiClient.unwrap(http.get(path(accountId, "/place-actions"), null));
    }

    /** Add an action link to the listing. */
    public JsonNode createPlaceAction(String accountId, String uri, String placeActionType, Boolean isPreferred) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("uri", uri);
        body.put("place_action_type", placeActionType);
        putIfPresent(body, "is_preferred", isPreferred);
        return ApiClient.unwrap(http.post(path(accountId, "/place-actions"), body));
    }

    /** Patch one action link; a null argument is left alone. */
    public JsonNode updatePlaceAction(String accountId, String linkId, String uri, Boolean isPreferred) {
        Map<String, Object> body = new LinkedHashMap<>();
        putIfPresent(body, "uri", uri);
        putIfPresent(body, "is_preferred", isPreferred);
        return ApiClient.unwrap(
                http.request("PATCH", path(accountId, "/place-actions/" + linkId), body, null));
    }

    /** Remove one action link. */
    public JsonNode deletePlaceAction(String accountId, String linkId) {
        return ApiClient.unwrap(http.delete(path(accountId, "/place-actions/" + linkId)));
    }

    /** The ways Google will let this location be verified. */
    public JsonNode getVerificationOptions(String accountId) {
        return getVerificationOptions(accountId, null);
    }

    public JsonNode getVerificationOptions(String accountId, String languageCode) {
        Map<String, Object> query = new LinkedHashMap<>();
        putIfPresent(query, "language_code", languageCode);
        return ApiClient.unwrap(http.get(path(accountId, "/verification"), query.isEmpty() ? null : query));
    }

    /**
     * Start a verification. {@code method} is {@code ADDRESS}, {@code EMAIL},
     * {@code PHONE_CALL}, {@code SMS}, {@code AUTO} or {@code VETTED_PARTNER};
     * the response names the pending verification to complete with the PIN.
     */
    public JsonNode startVerification(String accountId, String method, Map<String, Object> options) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("method", method);
        if (options != null) {
            options.forEach((key, value) -> putIfPresent(body, key, value));
        }
        return ApiClient.unwrap(http.post(path(accountId, "/verification/start"), body));
    }

    /** Complete a pending verification with the PIN Google sent. */
    public JsonNode completeVerification(String accountId, String verificationName, String pin) {
        return ApiClient.unwrap(http.post(
                path(accountId, "/verification/complete"),
                Map.of("verification_name", verificationName, "pin", pin)));
    }

    /**
     * Daily impressions, calls, direction requests and clicks for the range.
     * A null or empty {@code dailyMetrics} leaves the API's own default set.
     */
    public JsonNode getPerformance(String accountId, String startDate, String endDate, List<String> dailyMetrics) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("start_date", startDate);
        query.put("end_date", endDate);
        if (dailyMetrics != null && !dailyMetrics.isEmpty()) {
            query.put("daily_metrics", dailyMetrics);
        }
        return ApiClient.unwrap(http.get(path(accountId, "/performance"), query));
    }

    /** The search terms people used to find the listing, by month. */
    public JsonNode getSearchKeywords(String accountId, String startDate, String endDate, String pageToken) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("keywords", "true");
        query.put("start_date", startDate);
        query.put("end_date", endDate);
        putIfPresent(query, "page_token", pageToken);
        return ApiClient.unwrap(http.get(path(accountId, "/performance"), query));
    }

    /**
     * Hand the location to another workspace the caller owns. The connection
     * and every row keyed to it move in one transaction.
     */
    public JsonNode assign(String accountId, String workspaceId) {
        return ApiClient.unwrap(http.post(path(accountId, "/assign"), Map.of("workspace_id", workspaceId)));
    }

    private static String path(String accountId, String suffix) {
        return "/v1/accounts/" + accountId + "/gbp" + suffix;
    }

    /** An empty patch still has to be an object, not a missing body. */
    private static Object body(Map<String, Object> fields) {
        return fields == null ? Map.of() : fields;
    }

    private static void putIfPresent(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }
}
