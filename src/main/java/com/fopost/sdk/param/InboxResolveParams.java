package com.fopost.sdk.param;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * What to resolve in bulk: every unanswered item of one type in a workspace, optionally only on
 * some accounts or only items dated before an instant.
 *
 * <pre>{@code
 * int resolved = client.inbox().resolve(InboxResolveParams.of(workspaceId, "comment")
 *         .before(Instant.now().minus(Duration.ofDays(7))));
 * }</pre>
 */
public final class InboxResolveParams {

    private final String workspaceId;
    private final String type;
    private final List<String> accountIds = new ArrayList<>();
    private Instant before;

    private InboxResolveParams(String workspaceId, String type) {
        this.workspaceId = Objects.requireNonNull(workspaceId, "workspaceId");
        this.type = Objects.requireNonNull(type, "type");
    }

    /** {@code type} is {@code comment}, {@code mention}, {@code review} or {@code dm}. */
    public static InboxResolveParams of(String workspaceId, String type) {
        return new InboxResolveParams(workspaceId, type);
    }

    /** Only these connected accounts. */
    public InboxResolveParams accountId(String... accountIds) {
        return accountIds(accountIds == null ? null : Arrays.asList(accountIds));
    }

    public InboxResolveParams accountIds(List<String> accountIds) {
        if (accountIds != null) {
            for (String id : accountIds) {
                if (id != null && !id.isEmpty()) {
                    this.accountIds.add(id);
                }
            }
        }
        return this;
    }

    /** Only items the platform dates before this instant. */
    public InboxResolveParams before(Instant before) {
        this.before = before;
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspace_id", workspaceId);
        body.put("type", type);
        if (!accountIds.isEmpty()) {
            body.put("account_id", new ArrayList<>(accountIds));
        }
        Params.put(body, "before", Params.iso(before));
        return body;
    }
}
