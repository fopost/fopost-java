package com.fopost.sdk;

/** 403 — the key is valid but lacks the scope, the workspace, or a subscription. */
public class PermissionDeniedException extends FoPostException {
    private static final long serialVersionUID = 1L;

    public PermissionDeniedException(String message, int status, String code, Object body) {
        super(message, status, code, body);
    }

    /** True when the workspace behind the key has no active subscription. */
    public boolean isSubscriptionRequired() {
        return "subscription_required".equals(code());
    }
}
