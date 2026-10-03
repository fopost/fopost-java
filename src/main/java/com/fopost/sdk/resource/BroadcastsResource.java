package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Broadcast;
import com.fopost.sdk.model.BroadcastPage;
import com.fopost.sdk.model.BroadcastSent;
import com.fopost.sdk.model.RecipientPage;
import com.fopost.sdk.param.BroadcastParams;
import java.util.Map;

/**
 * One message into every conversation the workspace already has with a segment of its
 * contacts.
 *
 * <p>A broadcast is not a post and not a cold DM — every message lands in a direct-message
 * thread the contact already started.
 *
 * <p>Nothing is sent into a closed messaging window. Messenger and Instagram take a
 * business-initiated message only within 24 hours of the contact's last one, so recipients
 * outside it come back skipped with {@code window_closed} rather than attempted, which is
 * why the number sent is often lower than the audience. Telegram, Slack, Bluesky and Reddit
 * have no window.
 *
 * <p>Reading needs the {@code inbox} scope; {@link #send} and {@link #cancel} also need
 * {@code publish}.
 */
public final class BroadcastsResource {

    private final ApiClient http;

    public BroadcastsResource(ApiClient http) {
        this.http = http;
    }

    /** One page of broadcasts, newest first. */
    public BroadcastPage list() {
        return list(BroadcastParams.Filter.create());
    }

    /**
     * One page of broadcasts, newest first.
     *
     * <p>Omit the workspace to span every workspace the key can reach; each broadcast then
     * carries {@code workspaceId}.
     */
    public BroadcastPage list(BroadcastParams.Filter filter) {
        return http.convert(http.get("/v1/broadcasts", filter.toQuery()), BroadcastPage.class);
    }

    /**
     * One broadcast. A broadcast in a workspace the key cannot reach answers 404, exactly as
     * an id that never existed does.
     */
    public Broadcast get(String broadcastId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/broadcasts/" + broadcastId, null)), Broadcast.class);
    }

    /**
     * Write a broadcast without sending it.
     *
     * <p>Set {@code scheduledAt} to have it go out on its own at that time; otherwise call
     * {@link #send}.
     */
    public Broadcast create(BroadcastParams.Create params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/broadcasts", params.toBody())), Broadcast.class);
    }

    /** Patch a broadcast. Only a draft or scheduled broadcast can be edited. */
    public Broadcast update(String broadcastId, BroadcastParams.Update params) {
        return http.convert(
                ApiClient.unwrap(
                        http.request("PATCH", "/v1/broadcasts/" + broadcastId, params.toBody(), null)),
                Broadcast.class);
    }

    /**
     * Freeze the audience into a recipient list and start sending.
     *
     * <p>The returned {@code recipients} is how many contacts matched, not how many will be
     * messaged — the messaging window decides that. Needs the {@code publish} scope as well
     * as {@code inbox}.
     */
    public BroadcastSent send(String broadcastId) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/broadcasts/" + broadcastId + "/send", Map.of())),
                BroadcastSent.class);
    }

    /**
     * Stop a broadcast where it stands.
     *
     * <p>Anyone not yet written to stays unsent; messages already delivered are not recalled.
     * Needs the {@code publish} scope.
     */
    public Broadcast cancel(String broadcastId) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/broadcasts/" + broadcastId + "/cancel", Map.of())),
                Broadcast.class);
    }

    /** One row per contact, with what became of their message. */
    public RecipientPage recipients(String broadcastId) {
        return recipients(broadcastId, BroadcastParams.Recipients.create());
    }

    /**
     * One row per contact, with what became of their message. A skipped row carries its
     * reason.
     */
    public RecipientPage recipients(String broadcastId, BroadcastParams.Recipients filter) {
        return http.convert(
                http.get("/v1/broadcasts/" + broadcastId + "/recipients", filter.toQuery()),
                RecipientPage.class);
    }

    /**
     * Remove a broadcast and its recipient records. Messages already sent stay in the
     * conversations they went to.
     */
    public void delete(String broadcastId) {
        http.delete("/v1/broadcasts/" + broadcastId);
    }
}
