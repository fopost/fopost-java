package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** One execution of an automation. {@code logs} arrive from the single-run endpoint. */
public record AutomationRun(
        Long id,
        String automationId,
        String status,
        Integer currentStep,
        Map<String, Object> triggerEvent,
        Map<String, Object> context,
        Instant startedAt,
        Instant completedAt,
        String errorMessage,
        List<Log> logs) {

    public record Log(
            Long id,
            Integer stepPosition,
            String status,
            Map<String, Object> inputSnapshot,
            Map<String, Object> outputSnapshot,
            Instant startedAt,
            Instant completedAt,
            Long durationMs,
            String errorMessage) {}
}
