package com.fopost.sdk;

import com.fasterxml.jackson.databind.JsonNode;

/** 402 — no active subscription, or the AI credit balance ran out. */
public class PaymentRequiredException extends FoPostException {
    private static final long serialVersionUID = 1L;

    public PaymentRequiredException(String message, int status, String code, Object body) {
        super(message, status, code, body);
    }

    /** Where the API suggests sending the user to restore access, when it says. */
    public String upgradeUrl() {
        Object body = body();
        if (body instanceof JsonNode node) {
            JsonNode value = node.path("upgrade_url");
            if (value.isTextual()) {
                return value.asText();
            }
        }
        return null;
    }
}
