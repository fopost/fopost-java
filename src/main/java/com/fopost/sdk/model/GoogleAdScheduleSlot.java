package com.fopost.sdk.model;

/** One slot of a campaign's ad schedule. */
public record GoogleAdScheduleSlot(
        String id, String dayOfWeek, int startHour, int endHour, Double bidModifier) {}
