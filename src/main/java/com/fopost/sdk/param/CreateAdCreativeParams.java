package com.fopost.sdk.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A creative on a Meta ad account. {@code format} is image, video or carousel; {@code text} is the
 * primary copy. A video needs {@link #mediaUrl(String)}, a carousel two to ten {@link #card}s.
 */
public final class CreateAdCreativeParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateAdCreativeParams() {}

    public static CreateAdCreativeParams of(
            String workspaceId,
            String connectionId,
            String adAccountId,
            String pageId,
            String name,
            String format,
            String text) {
        CreateAdCreativeParams params = new CreateAdCreativeParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("adAccountId", adAccountId);
        params.body.put("pageId", pageId);
        params.body.put("name", name);
        params.body.put("format", format);
        params.body.put("text", text);
        return params;
    }

    public CreateAdCreativeParams headline(String headline) {
        body.put("headline", headline);
        return this;
    }

    public CreateAdCreativeParams destinationUrl(String destinationUrl) {
        body.put("destinationUrl", destinationUrl);
        return this;
    }

    /** LEARN_MORE (the default), SHOP_NOW, SIGN_UP, SUBSCRIBE, CONTACT_US, DOWNLOAD, and so on. */
    public CreateAdCreativeParams callToAction(String callToAction) {
        body.put("callToAction", callToAction);
        return this;
    }

    /** A query string appended to every link in the ad, e.g. {@code utm_source=meta&utm_medium=paid}. */
    public CreateAdCreativeParams urlTags(String urlTags) {
        body.put("urlTags", urlTags);
        return this;
    }

    /** A media library asset url: the image, or the video. */
    public CreateAdCreativeParams mediaUrl(String mediaUrl) {
        body.put("mediaUrl", mediaUrl);
        return this;
    }

    /** A video's poster frame, as a library image. Meta picks a frame when omitted. */
    public CreateAdCreativeParams thumbnailMediaUrl(String thumbnailMediaUrl) {
        body.put("thumbnailMediaUrl", thumbnailMediaUrl);
        return this;
    }

    public CreateAdCreativeParams card(String mediaUrl) {
        return card(mediaUrl, null, null, null);
    }

    /** One carousel card. {@code mediaUrl} is a library image; the rest may be null. */
    public CreateAdCreativeParams card(
            String mediaUrl, String destinationUrl, String headline, String description) {
        Map<String, Object> card = new LinkedHashMap<>();
        card.put("mediaUrl", mediaUrl);
        Params.put(card, "destinationUrl", destinationUrl);
        Params.put(card, "headline", headline);
        Params.put(card, "description", description);
        cards().add(card);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> cards() {
        return (List<Map<String, Object>>) body.computeIfAbsent("cards", ignored -> new ArrayList<>());
    }
}
