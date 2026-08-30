package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.AiCreditBalance;
import com.fopost.sdk.model.CaptionResult;
import com.fopost.sdk.model.RepurposeResult;
import com.fopost.sdk.model.RewriteResult;
import com.fopost.sdk.param.CaptionParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Caption assist, per-platform rewriting, and article fan-out.
 *
 * <p>Every call spends AI credits. Check the balance with {@link #credits()}; a
 * {@link com.fopost.sdk.PaymentRequiredException} means the plan has none left.
 *
 * <p>API keys reach {@link #credits()} and {@link #generateCaption}. {@link #rewrite} and
 * {@link #repurposeUrl} currently answer {@code 401} to an API key and need a signed-in
 * dashboard session; they are here so the surface is complete once the server opens them up.
 */
public final class AiResource {

    private final ApiClient http;

    public AiResource(ApiClient http) {
        this.http = http;
    }

    /** Credits remaining, used, and total for the current billing period. */
    public AiCreditBalance credits() {
        return http.convert(ApiClient.unwrap(http.get("/v1/ai/credits", null)), AiCreditBalance.class);
    }

    public CaptionResult generateCaption(CaptionParams params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/ai/generate-caption", params.toMap())), CaptionResult.class);
    }

    /** Rewrite one draft for each target platform. */
    public RewriteResult rewrite(String content, List<String> platforms) {
        return rewrite(content, platforms, null, null, null);
    }

    public RewriteResult rewrite(
            String content, List<String> platforms, String tone, String workspaceId, String brandVoiceId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", content);
        body.put("platforms", platforms);
        put(body, "tone", tone);
        put(body, "workspace_id", workspaceId);
        put(body, "brand_voice_id", brandVoiceId);
        return http.convert(ApiClient.unwrap(http.post("/v1/ai/rewrite", body)), RewriteResult.class);
    }

    /** Turn an article url into a post for each platform, in one call. */
    public RepurposeResult repurposeUrl(String url, List<String> platforms) {
        return repurposeUrl(url, platforms, null, null);
    }

    public RepurposeResult repurposeUrl(String url, List<String> platforms, String workspaceId, String brandVoiceId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("url", url);
        body.put("platforms", platforms);
        put(body, "workspace_id", workspaceId);
        put(body, "brand_voice_id", brandVoiceId);
        return http.convert(ApiClient.unwrap(http.post("/v1/ai/repurpose-url", body)), RepurposeResult.class);
    }

    private static void put(Map<String, Object> into, String key, Object value) {
        if (value != null) {
            into.put(key, value);
        }
    }
}
