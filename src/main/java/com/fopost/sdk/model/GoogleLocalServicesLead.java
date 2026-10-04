package com.fopost.sdk.model;

/** A lead from Local Services Ads, read live and never stored. */
public record GoogleLocalServicesLead(
        String id,
        String category,
        String service,
        String contactName,
        String phone,
        String email,
        String status,
        String type,
        String createdAt) {}
