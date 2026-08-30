package com.fopost.sdk;

/** 400 or 422 — the request body or query did not validate. */
public class ValidationException extends FoPostException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message, int status, String code, Object body) {
        super(message, status, code, body);
    }
}
