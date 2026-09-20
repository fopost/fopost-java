package com.fopost.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * One message and how long after the previous step it goes out.
 *
 * <p>{@code delayHours} on the first step is measured from the enrollment, so 0 means
 * straight away.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SequenceStep(double delayHours, String text, String mediaId) {

    public static SequenceStep of(double delayHours, String text) {
        return new SequenceStep(delayHours, text, null);
    }
}
