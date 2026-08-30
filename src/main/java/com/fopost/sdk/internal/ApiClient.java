package com.fopost.sdk.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fopost.sdk.AuthenticationException;
import com.fopost.sdk.FoPostException;
import com.fopost.sdk.NotFoundException;
import com.fopost.sdk.PaymentRequiredException;
import com.fopost.sdk.PermissionDeniedException;
import com.fopost.sdk.RateLimitException;
import com.fopost.sdk.ValidationException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal HTTP layer: auth headers, query encoding, JSON coding, the {@code {"data": ...}}
 * envelope unwrap, retries on 429, and the mapping from a failed response to an exception.
 */
public final class ApiClient {

    public static final String DEFAULT_BASE_URL = "https://api.fopost.com";
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    public static final int DEFAULT_MAX_RETRIES = 3;

    private static final Duration MAX_RETRY_WAIT = Duration.ofSeconds(60);
    private static final String USER_AGENT = "fopost-java/" + Version.VALUE;

    private final String apiKey;
    private final String baseUrl;
    private final int maxRetries;
    private final Transport transport;
    private final Sleeper sleeper;

    public ApiClient(String apiKey, String baseUrl, int maxRetries, Transport transport, Sleeper sleeper) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("fopost: an API key is required");
        }
        if (maxRetries < 1) {
            throw new IllegalArgumentException("fopost: maxRetries must be at least 1");
        }
        this.apiKey = apiKey;
        this.baseUrl = stripTrailingSlash(baseUrl == null ? DEFAULT_BASE_URL : baseUrl);
        this.maxRetries = maxRetries;
        this.transport = transport;
        this.sleeper = sleeper == null ? Sleeper.DEFAULT : sleeper;
    }

    public String baseUrl() {
        return baseUrl;
    }

    // ─── Verbs ────────────────────────────────────────────────────────────────

    public JsonNode get(String path, Map<String, Object> query) {
        return request("GET", path, null, query);
    }

    public JsonNode post(String path, Object body) {
        return request("POST", path, body, null);
    }

    public JsonNode post(String path, Object body, Map<String, Object> query) {
        return request("POST", path, body, query);
    }

    public JsonNode put(String path, Object body) {
        return request("PUT", path, body, null);
    }

    public JsonNode delete(String path) {
        return request("DELETE", path, null, null);
    }

    /** Send a request and return the decoded body, retrying while the API answers 429. */
    public JsonNode request(String method, String path, Object body, Map<String, Object> query) {
        byte[] encoded = null;
        String contentType = null;
        if (body instanceof Multipart multipart) {
            contentType = multipart.contentType();
            encoded = multipart.build();
        } else if (body != null) {
            contentType = "application/json";
            encoded = encode(body);
        }
        return send(method, path, encoded, contentType, query);
    }

    private JsonNode send(String method, String path, byte[] body, String contentType, Map<String, Object> query) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Accept", "application/json");
        headers.put("X-API-Key", apiKey);
        headers.put("User-Agent", USER_AGENT);
        if (contentType != null) {
            headers.put("Content-Type", contentType);
        }

        String url = url(path, query);
        HttpRequestData request = new HttpRequestData(method, url, headers, body);

        int attempt = 0;
        while (true) {
            attempt++;
            HttpResponseData response;
            try {
                response = transport.send(request);
            } catch (IOException e) {
                throw new FoPostException("fopost: request to " + url + " failed: " + e.getMessage(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new FoPostException("fopost: request to " + url + " was interrupted", e);
            }

            if (response.status() == 429 && attempt < maxRetries) {
                Duration wait = retryAfter(response);
                sleeper.sleep(wait == null ? Duration.ofSeconds(1) : min(wait, MAX_RETRY_WAIT));
                continue;
            }
            return decode(response);
        }
    }

    // ─── Decoding ─────────────────────────────────────────────────────────────

    private JsonNode decode(HttpResponseData response) {
        JsonNode body = NullNode.getInstance();
        byte[] raw = response.body();
        if (response.status() != 204 && raw != null && raw.length > 0) {
            try {
                body = Json.MAPPER.readTree(raw);
            } catch (IOException e) {
                if (response.status() >= 200 && response.status() < 300) {
                    throw new FoPostException(
                            "fopost: expected a JSON response, got " + response.header("content-type"),
                            response.status(),
                            null,
                            response.bodyAsString());
                }
                throw errorFor(response.status(), null, response.bodyAsString(), retryAfter(response));
            }
        }

        if (response.status() >= 200 && response.status() < 300) {
            return body;
        }
        throw errorFor(response.status(), body, messageOf(body, response.status()), retryAfter(response));
    }

    private static FoPostException errorFor(int status, JsonNode body, String message, Duration retryAfter) {
        String code = body != null && body.path("error").isTextual() ? body.path("error").asText() : null;
        return switch (status) {
            case 400, 422 -> new ValidationException(message, status, code, body);
            case 401 -> new AuthenticationException(message, status, code, body);
            case 402 -> new PaymentRequiredException(message, status, code, body);
            case 403 -> new PermissionDeniedException(message, status, code, body);
            case 404 -> new NotFoundException(message, status, code, body);
            case 429 -> new RateLimitException(message, status, code, body, retryAfter);
            default -> new FoPostException(message, status, code, body);
        };
    }

    private static String messageOf(JsonNode body, int status) {
        if (body != null) {
            if (body.path("message").isTextual() && !body.path("message").asText().isEmpty()) {
                return body.path("message").asText();
            }
            if (body.path("error").isTextual() && !body.path("error").asText().isEmpty()) {
                return body.path("error").asText();
            }
        }
        return "HTTP " + status;
    }

    /**
     * Peel the {@code {"data": ...}} envelope the API wraps most responses in. Some endpoints
     * (create a post, get a post, labels) answer with the resource bare, so the envelope is
     * removed only when it is actually there.
     */
    public static JsonNode unwrap(JsonNode body) {
        if (body != null && body.isObject() && body.has("data")) {
            return body.get("data");
        }
        return body;
    }

    public <T> T convert(JsonNode node, Class<T> type) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        return Json.MAPPER.convertValue(node, type);
    }

    public <T> T convert(JsonNode node, TypeReference<T> type) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        return Json.MAPPER.convertValue(node, type);
    }

    public <T> List<T> convertList(JsonNode node, Class<T> type) {
        if (node == null || !node.isArray()) {
            return List.of();
        }
        return Json.MAPPER.convertValue(
                node, Json.MAPPER.getTypeFactory().constructCollectionType(List.class, type));
    }

    private static byte[] encode(Object body) {
        try {
            return Json.MAPPER.writeValueAsBytes(body);
        } catch (IOException e) {
            throw new FoPostException("fopost: could not encode the request body", e);
        }
    }

    // ─── URL and headers ──────────────────────────────────────────────────────

    private String url(String path, Map<String, Object> query) {
        StringBuilder url = new StringBuilder();
        if (path.startsWith("http://") || path.startsWith("https://")) {
            url.append(path);
        } else {
            url.append(baseUrl).append(path.startsWith("/") ? path : "/" + path);
        }

        if (query != null && !query.isEmpty()) {
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                Object value = entry.getValue();
                if (value == null) {
                    continue;
                }
                if (value instanceof Collection<?> values) {
                    for (Object item : values) {
                        appendParam(queryString, entry.getKey(), item);
                    }
                } else {
                    appendParam(queryString, entry.getKey(), value);
                }
            }
            if (queryString.length() > 0) {
                url.append('?').append(queryString);
            }
        }
        return url.toString();
    }

    private static void appendParam(StringBuilder into, String key, Object value) {
        if (value == null) {
            return;
        }
        if (into.length() > 0) {
            into.append('&');
        }
        into.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                .append('=')
                .append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8));
    }

    /** {@code Retry-After} is either delta-seconds or an HTTP date. */
    static Duration retryAfter(HttpResponseData response) {
        String raw = response.header("Retry-After");
        if (raw == null || raw.isBlank()) {
            return null;
        }
        raw = raw.trim();
        try {
            long seconds = (long) Math.max(0, Double.parseDouble(raw));
            return Duration.ofSeconds(seconds);
        } catch (NumberFormatException ignored) {
            // Fall through to the date form.
        }
        try {
            ZonedDateTime target = ZonedDateTime.parse(raw, DateTimeFormatter.RFC_1123_DATE_TIME);
            Duration delta = Duration.between(ZonedDateTime.now(target.getZone()), target);
            return delta.isNegative() ? Duration.ZERO : delta;
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static Duration min(Duration a, Duration b) {
        return a.compareTo(b) <= 0 ? a : b;
    }

    private static String stripTrailingSlash(String value) {
        String trimmed = value;
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
