package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A hosted connect link to mint. {@code externalUserId} and {@code platforms} are required;
 * {@code workspaceId} may be left out for a key bound to one workspace.
 *
 * <pre>{@code
 * ConnectSessionParams.of("user_42", List.of("linkedin", "threads"))
 *     .workspaceId(workspaceId)
 *     .redirectUrl("https://yourbrand.com/connected")
 *     .branding("Your Brand", "https://yourbrand.com/logo.png");
 * }</pre>
 */
public final class ConnectSessionParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private ConnectSessionParams(String externalUserId, List<String> platforms) {
        body.put("externalUserId", externalUserId);
        body.put("platforms", platforms);
    }

    public static ConnectSessionParams of(String externalUserId, List<String> platforms) {
        return new ConnectSessionParams(externalUserId, platforms);
    }

    public ConnectSessionParams workspaceId(String workspaceId) {
        body.put("workspaceId", workspaceId);
        return this;
    }

    /** Where the user lands once connected. Its domain has to be on the workspace redirect allowlist. */
    public ConnectSessionParams redirectUrl(String redirectUrl) {
        body.put("redirectUrl", redirectUrl);
        return this;
    }

    /** The name and logo the hosted page carries. Either may be null. */
    public ConnectSessionParams branding(String name, String logoUrl) {
        Map<String, Object> branding = new LinkedHashMap<>();
        Params.put(branding, "name", name);
        Params.put(branding, "logoUrl", logoUrl);
        body.put("branding", branding);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
