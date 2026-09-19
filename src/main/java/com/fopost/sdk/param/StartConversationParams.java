package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A new DM: to a handle from an account, or as a private reply to an inbox comment. */
public final class StartConversationParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private StartConversationParams() {}

    /** Message {@code handle} from {@code accountId}. Only where the account's {@code canStartConversation} is true. */
    public static StartConversationParams toHandle(String accountId, String handle, String text) {
        StartConversationParams params = new StartConversationParams();
        params.body.put("account_id", accountId);
        params.body.put("handle", handle);
        params.body.put("text", text);
        return params;
    }

    /** Answer an inbox comment privately, by DM. Only where the item's {@code canPrivateReply} is true. */
    public static StartConversationParams privateReply(String commentId, String text) {
        StartConversationParams params = new StartConversationParams();
        params.body.put("comment_id", commentId);
        params.body.put("text", text);
        return params;
    }

    /** Media library ids to attach, at most 10. */
    public StartConversationParams mediaIds(List<String> mediaIds) {
        body.put("media_ids", List.copyOf(mediaIds));
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
