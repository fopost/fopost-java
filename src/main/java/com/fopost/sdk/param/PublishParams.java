package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Options for a publish: a subset of the post's accounts, and whether to only rehearse it. */
public final class PublishParams {

    private List<String> accountIds;
    private Boolean dryRun;

    public static PublishParams create() {
        return new PublishParams();
    }

    /** Publish to these accounts only, instead of every account on the post. */
    public PublishParams accountIds(List<String> accountIds) {
        this.accountIds = accountIds;
        return this;
    }

    public PublishParams accountIds(String... accountIds) {
        return accountIds(List.of(accountIds));
    }

    /** Run every check and report what would happen, without queueing anything. */
    public PublishParams dryRun(boolean dryRun) {
        this.dryRun = dryRun;
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> body = new LinkedHashMap<>();
        Params.put(body, "accountIds", accountIds);
        if (dryRun != null) {
            body.put("options", Map.of("dryRun", dryRun));
        }
        return body;
    }
}
