package com.fopost.sdk.model;

/** The result of re-checking one account's stored credentials against its platform. */
public record AccountValidation(String accountId, String platform, Boolean valid, String healthStatus) {

    public boolean isValid() {
        return Boolean.TRUE.equals(valid);
    }
}
