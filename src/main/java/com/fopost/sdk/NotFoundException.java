package com.fopost.sdk;

/** 404 — no such resource, or it sits outside the key's reach. */
public class NotFoundException extends FoPostException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(String message, int status, String code, Object body) {
        super(message, status, code, body);
    }
}
