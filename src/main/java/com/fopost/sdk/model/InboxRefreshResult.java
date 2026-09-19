package com.fopost.sdk.model;

import java.util.List;

/** What a manual inbox poll found. {@code dmReconnect} lists accounts whose DM grant needs renewing. */
public record InboxRefreshResult(
        Integer accountsPolled, Integer newItems, Integer rateLimited, List<DmReconnect> dmReconnect) {

    public record DmReconnect(String platform, String account) {}
}
