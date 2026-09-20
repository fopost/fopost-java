package com.fopost.sdk.model;

/** Whether the cart and catalog show on the number, and which catalog is linked. */
public record WhatsappCommerceSettings(Boolean cartEnabled, Boolean catalogVisible, String catalogId) {}
