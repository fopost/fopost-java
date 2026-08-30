package com.fopost.sdk;

/** 401 — missing, invalid, or expired API key. */
public class AuthenticationException extends FoPostException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message, int status, String code, Object body) {
        super(message, status, code, body);
    }
}
