package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.ConnectSession;
import com.fopost.sdk.param.ConnectSessionParams;

/**
 * Hosted connect links: a single-use page, open for one hour, where one of your end users connects
 * a social account into your workspace with no FoPost login. The account is tagged with the
 * {@code externalUserId} you pass. Needs the {@code accounts} scope.
 *
 * <pre>{@code
 * ConnectSession session = client.connectSessions()
 *         .create(ConnectSessionParams.of("user_42", List.of("linkedin")).workspaceId(workspaceId));
 * // hand session.url() to the user, then poll:
 * ConnectSession later = client.connectSessions().get(session.id());
 * }</pre>
 */
public final class ConnectSessionsResource {

    private final ApiClient http;

    public ConnectSessionsResource(ApiClient http) {
        this.http = http;
    }

    /** Mint a link. {@code url} is on the returned session and is never shown again. */
    public ConnectSession create(ConnectSessionParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/connect-sessions", params.toMap())), ConnectSession.class);
    }

    /** The session's state, and the connected account once it has been used. */
    public ConnectSession get(String sessionId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/connect-sessions/" + sessionId, null)), ConnectSession.class);
    }
}
