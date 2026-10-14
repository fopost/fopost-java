package com.fopost.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.Json;

/**
 * A verified webhook delivery: the event name, its body and when it was sent.
 *
 * <pre>{@code
 * WebhookEnvelope envelope = WebhookVerifier.verify(rawBody, headers, secret);
 * if (WebhookEvents.ARTICLE_PUBLISHED.equals(envelope.event())) {
 *     BlogArticlePublished article = envelope.data(BlogArticlePublished.class);
 * }
 * }</pre>
 */
public record WebhookEnvelope(String event, JsonNode data, String timestamp) {

    /** The body as a typed record, such as {@link com.fopost.sdk.model.BlogArticlePublished}. */
    public <T> T data(Class<T> type) {
        if (data == null || data.isNull() || data.isMissingNode()) {
            return null;
        }
        return Json.MAPPER.convertValue(data, type);
    }
}
