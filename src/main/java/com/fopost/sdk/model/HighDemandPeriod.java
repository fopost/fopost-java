package com.fopost.sdk.model;

/** A window the network should expect heavier spend over. */
public record HighDemandPeriod(
        String id,
        String startAt,
        String endAt,
        Double budgetValue,
        String budgetValueType) {}
