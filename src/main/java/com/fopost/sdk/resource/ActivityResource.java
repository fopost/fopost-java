package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.ActivityEvent;
import com.fopost.sdk.model.ActivityPage;
import com.fopost.sdk.param.ActivityParams;
import java.util.List;

/** What happened in a workspace, including the security audit log. */
public final class ActivityResource {

    private final ApiClient http;

    public ActivityResource(ApiClient http) {
        this.http = http;
    }

    public ActivityPage list() {
        return list(ActivityParams.create());
    }

    /**
     * Activity newest first. {@link ActivityParams#KIND_SECURITY} is the audit log: members
     * joining, leaving or changing role and access, and changes to two-step verification,
     * passkeys, single sign-on and signed-in devices. Those rows are append-only and never expire.
     */
    public ActivityPage list(ActivityParams params) {
        // The response carries meta beside data, so it is read whole rather than unwrapped.
        JsonNode body = http.get("/v1/activity", params == null ? null : params.toQuery());
        List<ActivityEvent> events = http.convertList(body.path("data"), ActivityEvent.class);
        JsonNode cursor = body.path("meta").path("next_cursor");
        return new ActivityPage(events, cursor.isNull() || cursor.isMissingNode() ? null : cursor.asText());
    }
}
