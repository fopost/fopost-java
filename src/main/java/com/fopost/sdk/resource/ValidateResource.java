package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.LengthValidation;
import com.fopost.sdk.model.MediaValidation;
import com.fopost.sdk.model.PostValidation;
import com.fopost.sdk.param.ValidatePostParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Check a draft, a text length, or a media url against the platform rules without creating a post.
 *
 * <pre>{@code
 * PostValidation check = client.validate().post(
 *         ValidatePostParams.of("twitter", "linkedin").content("Shipping today"));
 * if (!check.isReady()) {
 *     check.platforms().forEach(p -> System.out.println(p.platform() + ": " + p.issues()));
 * }
 * }</pre>
 *
 * <p>Nothing is stored. Every call needs the {@code posts} scope.
 */
public final class ValidateResource {

    private final ApiClient http;

    public ValidateResource(ApiClient http) {
        this.http = http;
    }

    /** Per-platform blockers, score and advisory signals for a draft. */
    public PostValidation post(ValidatePostParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/validate/post", params.toMap())), PostValidation.class);
    }

    /** How a text measures against each platform's limit. */
    public LengthValidation length(String text, List<String> platforms) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("text", text);
        body.put("platforms", platforms);
        return http.convert(ApiClient.unwrap(http.post("/v1/validate/length", body)), LengthValidation.class);
    }

    public LengthValidation length(String text, String... platforms) {
        return length(text, List.of(platforms));
    }

    /** Fetch a public url and check it as an attachment. Answers 200 even when a check fails. */
    public MediaValidation media(String url) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url", url);
        return http.convert(ApiClient.unwrap(http.post("/v1/validate/media", body)), MediaValidation.class);
    }
}
