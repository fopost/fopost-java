package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A reply carrying media or quick replies. {@code text} is required unless {@code mediaIds} is given;
 * either list also needs the {@code publish} scope.
 */
public final class InboxReplyParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    public static InboxReplyParams create() {
        return new InboxReplyParams();
    }

    public InboxReplyParams text(String text) {
        body.put("text", text);
        return this;
    }

    /** Media library ids to attach to a DM, at most 10. Only where {@code canSendMedia} is true. */
    public InboxReplyParams mediaIds(List<String> mediaIds) {
        body.put("media_ids", List.copyOf(mediaIds));
        return this;
    }

    /** Answer buttons under a DM, at most 13 of up to 20 characters. Only where {@code canQuickReply} is true. */
    public InboxReplyParams quickReplies(List<String> quickReplies) {
        body.put("quick_replies", List.copyOf(quickReplies));
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
