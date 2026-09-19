package com.fopost.sdk.model;

/** The settled item and where the reply landed on the platform. */
public record InboxReplyResult(InboxItem item, Reply reply) {

    public record Reply(String externalId, String externalUrl) {}
}
