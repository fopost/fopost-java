package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.LogEntry;
import com.fopost.sdk.model.LogPage;
import com.fopost.sdk.model.LogSummary;
import com.fopost.sdk.param.LogParams;
import java.util.List;

/** The request log: every API call members made, kept for 30 days. Needs the {@code workspaces} scope. */
public final class LogsResource {

    private final ApiClient http;

    public LogsResource(ApiClient http) {
        this.http = http;
    }

    public LogPage list() {
        return list(LogParams.create());
    }

    /** Requests newest first, with route, status code, duration and any error message. */
    public LogPage list(LogParams params) {
        // The response carries meta and summary beside data, so it is read whole rather than unwrapped.
        JsonNode body = http.get("/v1/logs", params == null ? null : params.toQuery());
        List<LogEntry> entries = http.convertList(body.path("data"), LogEntry.class);
        JsonNode meta = body.path("meta");
        JsonNode cursor = meta.path("next_cursor");
        return new LogPage(
                entries,
                cursor.isNull() || cursor.isMissingNode() ? null : cursor.asText(),
                meta.path("has_more").asBoolean(false),
                http.convert(body.path("summary"), LogSummary.class));
    }
}
