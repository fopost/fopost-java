package com.fopost.sdk.model;

/** Whether a business public key is registered. The key itself never comes back. */
public record WhatsappEncryptionKeyStatus(boolean hasKey, String signatureStatus) {}
