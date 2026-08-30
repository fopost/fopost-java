package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An account to connect with credentials you already hold.
 *
 * <p>Most accounts are connected through the OAuth flow in the dashboard instead; this endpoint
 * covers the platforms where you bring your own token.
 */
public final class CreateAccountParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateAccountParams() {}

    public static CreateAccountParams of(String workspaceId, String platform, String username, String name) {
        CreateAccountParams params = new CreateAccountParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("platform", platform);
        params.body.put("username", username);
        params.body.put("name", name);
        return params;
    }

    public CreateAccountParams avatar(String avatar) {
        body.put("avatar", avatar);
        return this;
    }

    /** Platform credentials, in the shape that platform expects. */
    public CreateAccountParams credentials(Map<String, Object> credentials) {
        body.put("credentials", credentials);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
