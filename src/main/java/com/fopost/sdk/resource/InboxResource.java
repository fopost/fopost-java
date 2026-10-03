package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.internal.Json;
import com.fopost.sdk.model.InboxAccount;
import com.fopost.sdk.model.InboxApproval;
import com.fopost.sdk.model.InboxApprovalDecision;
import com.fopost.sdk.model.InboxConversation;
import com.fopost.sdk.model.InboxConversationStarted;
import com.fopost.sdk.model.InboxHandover;
import com.fopost.sdk.model.InboxItem;
import com.fopost.sdk.model.InboxPage;
import com.fopost.sdk.model.InboxPageMeta;
import com.fopost.sdk.model.InboxPlatform;
import com.fopost.sdk.model.InboxRefreshResult;
import com.fopost.sdk.model.InboxReplyResult;
import com.fopost.sdk.model.InboxThread;
import com.fopost.sdk.param.InboxListParams;
import com.fopost.sdk.param.InboxReplyParams;
import com.fopost.sdk.param.InboxThreadParams;
import com.fopost.sdk.param.StartConversationParams;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Comments, mentions, reviews and direct messages on the connected accounts.
 *
 * <pre>{@code
 * InboxPage<InboxItem> unread = client.inbox().list(
 *         InboxListParams.create().workspaceId(workspaceId).state("unread"));
 * for (InboxItem item : unread) {
 *     if (Boolean.TRUE.equals(item.canReply())) {
 *         client.inbox().reply(item.id(), "Thanks for asking, sent you a DM.");
 *     }
 * }
 * }</pre>
 *
 * <p>Every call needs the {@code inbox} scope on an API key. The calls that act on the platform as
 * the account (like, pin, react, edit, start a conversation, typing, a reply with media or quick
 * replies, deleting our own reply) also need the {@code publish} scope.
 */
public final class InboxResource {

    private final ApiClient http;

    public InboxResource(ApiClient http) {
        this.http = http;
    }

    // ─── Reading ──────────────────────────────────────────────────────────────

    public InboxPage<InboxItem> list() {
        return list(InboxListParams.create());
    }

    /** One page of items, newest first. */
    public InboxPage<InboxItem> list(InboxListParams params) {
        return page(http.get("/v1/inbox", params.toQuery()), InboxItem.class);
    }

    public InboxPage<InboxThread> threads() {
        return threads(InboxThreadParams.create());
    }

    /**
     * One row per platform post that has collected comments, per post the account was tagged in
     * with {@code kind("mentions")}, or per review with {@code kind("reviews")}. Fetch a thread's
     * items with {@link #list} filtered by
     * {@code accountId} and {@code postExternalId}.
     */
    public InboxPage<InboxThread> threads(InboxThreadParams params) {
        return page(http.get("/v1/inbox/posts", params.toQuery()), InboxThread.class);
    }

    public InboxPage<InboxConversation> conversations() {
        return conversations(InboxThreadParams.create());
    }

    /**
     * One row per direct-message thread, latest first. Fetch a thread with {@link #list} filtered
     * by {@code accountId} and {@code conversationId}. {@code kind} is ignored here.
     */
    public InboxPage<InboxConversation> conversations(InboxThreadParams params) {
        return page(http.get("/v1/inbox/conversations", params.toQuery()), InboxConversation.class);
    }

    public int unreadCount() {
        return unreadCount(null);
    }

    public int unreadCount(String workspaceId) {
        JsonNode body = http.get("/v1/inbox/unread-count", workspaceQuery(workspaceId));
        return body.path("count").asInt(0);
    }

    public List<InboxAccount> accounts() {
        return accounts(null);
    }

    /** Every active account, flagged with whether comments and DMs can be read for it yet. */
    public List<InboxAccount> accounts(String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/inbox/accounts", workspaceQuery(workspaceId))), InboxAccount.class);
    }

    /** Which networks feed the inbox today, and which are coming. Not tenant data. */
    public List<InboxPlatform> platforms() {
        return http.convertList(ApiClient.unwrap(http.get("/v1/inbox/platforms", null)), InboxPlatform.class);
    }

    // ─── Reading state ────────────────────────────────────────────────────────

    /** Reads every comment under one platform post on the account. Returns how many items changed. */
    public int markThreadRead(String workspaceId, String accountId, String postExternalId) {
        Map<String, Object> body = readBody(workspaceId, accountId);
        body.put("post_external_id", postExternalId);
        return markRead(body);
    }

    /** Reads every message in one DM thread on the account. Returns how many items changed. */
    public int markConversationRead(String workspaceId, String accountId, String conversationId) {
        Map<String, Object> body = readBody(workspaceId, accountId);
        body.put("conversation_id", conversationId);
        return markRead(body);
    }

    /**
     * Poll every inbox-capable account in the workspace now instead of waiting for the next
     * scheduled run. Platforms that bill per read are read on this call.
     */
    public InboxRefreshResult refresh(String workspaceId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspace_id", workspaceId);
        return http.convert(ApiClient.unwrap(http.post("/v1/inbox/refresh", body)), InboxRefreshResult.class);
    }

    /** {@code state} is unread, read or resolved. Snoozing needs {@link #update(String, String, Instant)}. */
    public InboxItem update(String itemId, String state) {
        return update(itemId, state, null);
    }

    /** Mark the item unread, read, resolved, or snoozed until {@code snoozedUntil}, a time in the future. */
    public InboxItem update(String itemId, String state, Instant snoozedUntil) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("state", state);
        if (snoozedUntil != null) {
            body.put("snoozedUntil", snoozedUntil.toString());
        }
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", "/v1/inbox/" + itemId, body, null)), InboxItem.class);
    }

    /** Edit our own comment on the platform. Only where {@code canEdit} is true; also needs {@code publish}. */
    public InboxItem editComment(String itemId, String text) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("text", text);
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", "/v1/inbox/" + itemId, body, null)), InboxItem.class);
    }

    // ─── Acting on an item ────────────────────────────────────────────────────

    /** Send the reply on the platform as the connected account. Only where {@code canReply} is true. */
    public InboxReplyResult reply(String itemId, String text) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("text", text);
        return http.convert(
                ApiClient.unwrap(http.post("/v1/inbox/" + itemId + "/reply", body)), InboxReplyResult.class);
    }

    /** A reply with media or quick replies. Only where {@code canReply} is true. */
    public InboxReplyResult reply(String itemId, InboxReplyParams params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/inbox/" + itemId + "/reply", params.toMap())), InboxReplyResult.class);
    }

    public InboxItem hide(String itemId) {
        return http.convert(ApiClient.unwrap(http.post("/v1/inbox/" + itemId + "/hide", null)), InboxItem.class);
    }

    public InboxItem unhide(String itemId) {
        return http.convert(ApiClient.unwrap(http.post("/v1/inbox/" + itemId + "/unhide", null)), InboxItem.class);
    }

    /**
     * Remove the comment from the platform, whether someone else wrote it or it is our own reply. Only
     * where {@code canDelete} is true; deleting our own reply also needs {@code publish}.
     */
    public boolean delete(String itemId) {
        return ApiClient.unwrap(http.delete("/v1/inbox/" + itemId)).path("deleted").asBoolean(false);
    }

    /** A like, an upvote on Reddit, a favourite on Mastodon. Only where {@code canLike} is true. */
    public InboxItem like(String itemId) {
        return http.convert(ApiClient.unwrap(http.post("/v1/inbox/" + itemId + "/like", null)), InboxItem.class);
    }

    public InboxItem unlike(String itemId) {
        return http.convert(ApiClient.unwrap(http.post("/v1/inbox/" + itemId + "/unlike", null)), InboxItem.class);
    }

    /** Pin our own comment. Only where {@code canPin} is true. */
    public InboxItem pin(String itemId) {
        return http.convert(ApiClient.unwrap(http.post("/v1/inbox/" + itemId + "/pin", null)), InboxItem.class);
    }

    public InboxItem unpin(String itemId) {
        return http.convert(ApiClient.unwrap(http.post("/v1/inbox/" + itemId + "/unpin", null)), InboxItem.class);
    }

    /** React to a message with an emoji, or {@code null} to remove ours. Only where {@code canReact} is true. */
    public InboxItem react(String itemId, String reaction) {
        // An ObjectNode keeps an explicit null, which the mapper drops from a Map.
        ObjectNode body = Json.MAPPER.createObjectNode();
        body.put("reaction", reaction);
        return http.convert(ApiClient.unwrap(http.post("/v1/inbox/" + itemId + "/react", body)), InboxItem.class);
    }

    // ─── Conversations ────────────────────────────────────────────────────────

    /** Open a DM, by handle or as a private reply to a comment. Also needs {@code publish}. */
    public InboxConversationStarted startConversation(StartConversationParams params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/inbox/conversations", params.toMap())), InboxConversationStarted.class);
    }

    /** Show the typing indicator in a DM thread. */
    public boolean setTyping(String conversationId, String accountId) {
        return setTyping(conversationId, accountId, true);
    }

    /** Show or clear the typing indicator in a DM thread. Returns whether it is now shown. Also needs {@code publish}. */
    public boolean setTyping(String conversationId, String accountId, boolean on) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("account_id", accountId);
        body.put("on", on);
        return ApiClient.unwrap(http.post("/v1/inbox/conversations/" + conversationId + "/typing", body))
                .path("typing")
                .asBoolean(false);
    }

    /** Take a Messenger thread back from whichever app holds it. Also needs {@code publish}. */
    public InboxHandover takeThreadControl(String conversationId, String accountId) {
        return handover(conversationId, accountId, null, null);
    }

    /** Pass a Messenger thread to another Meta app. Also needs {@code publish}. */
    public InboxHandover passThreadControl(String conversationId, String accountId, String appId) {
        return handover(conversationId, accountId, appId, null);
    }

    /**
     * Pass a Messenger thread to another Meta app, or take it back when {@code appId} is null.
     * Also needs {@code publish}.
     */
    public InboxHandover handover(String conversationId, String accountId, String appId, String metadata) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("account_id", accountId);
        if (appId != null) {
            body.put("app_id", appId);
        }
        if (metadata != null) {
            body.put("metadata", metadata);
        }
        return http.convert(
                ApiClient.unwrap(http.post("/v1/inbox/conversations/" + conversationId + "/handover", body)),
                InboxHandover.class);
    }

    // ─── Approvals ────────────────────────────────────────────────────────────

    public List<InboxApproval> listApprovals() {
        return listApprovals(null);
    }

    /** Replies an automation or the agent drafted that a person still has to send. */
    public List<InboxApproval> listApprovals(String workspaceId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/inbox/approvals", workspaceQuery(workspaceId))), InboxApproval.class);
    }

    /** Send the draft as it stands. */
    public InboxApprovalDecision approveReply(long approvalId) {
        return approveReply(approvalId, null);
    }

    /** Send {@code text} in place of the draft. */
    public InboxApprovalDecision approveReply(long approvalId, String text) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (text != null) {
            body.put("text", text);
        }
        return http.convert(
                ApiClient.unwrap(http.post("/v1/inbox/approvals/" + approvalId + "/approve", body)),
                InboxApprovalDecision.class);
    }

    public InboxApprovalDecision rejectReply(long approvalId) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/inbox/approvals/" + approvalId + "/reject", null)),
                InboxApprovalDecision.class);
    }

    // ─── Plumbing ─────────────────────────────────────────────────────────────

    private <T> InboxPage<T> page(JsonNode body, Class<T> type) {
        return new InboxPage<>(
                http.convertList(body.path("data"), type), http.convert(body.path("meta"), InboxPageMeta.class));
    }

    private int markRead(Map<String, Object> body) {
        return ApiClient.unwrap(http.post("/v1/inbox/read", body)).path("updated").asInt(0);
    }

    private static Map<String, Object> readBody(String workspaceId, String accountId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspace_id", workspaceId);
        body.put("account_id", accountId);
        return body;
    }

    private static Map<String, Object> workspaceQuery(String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (workspaceId != null) {
            query.put("workspace_id", workspaceId);
        }
        return query;
    }
}
