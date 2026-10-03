package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Enrolled;
import com.fopost.sdk.model.EnrollmentPage;
import com.fopost.sdk.model.Sequence;
import com.fopost.sdk.model.SequencePage;
import com.fopost.sdk.model.Unenrolled;
import com.fopost.sdk.param.BroadcastParams;
import java.util.List;
import java.util.Map;

/**
 * A series of messages, each a delay after the one before, walked per enrolled contact.
 *
 * <p>The messaging window applies to every step. A step that comes due outside it is skipped
 * rather than sent, and the enrollment carries on — so someone can complete a sequence
 * having received only some of its messages.
 *
 * <p>Reading needs the {@code inbox} scope; {@link #enroll} and {@link #unenroll} also need
 * {@code publish}.
 */
public final class SequencesResource {

    private final ApiClient http;

    public SequencesResource(ApiClient http) {
        this.http = http;
    }

    /** One page of sequences. */
    public SequencePage list() {
        return list(BroadcastParams.SequenceFilter.create());
    }

    /** One page of sequences. */
    public SequencePage list(BroadcastParams.SequenceFilter filter) {
        return http.convert(http.get("/v1/sequences", filter.toQuery()), SequencePage.class);
    }

    /** One sequence. */
    public Sequence get(String sequenceId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/sequences/" + sequenceId, null)), Sequence.class);
    }

    /** Write a sequence. Creating one enrolls nobody. */
    public Sequence create(BroadcastParams.CreateSequence params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/sequences", params.toBody())), Sequence.class);
    }

    /**
     * Patch a sequence. Pausing stops every enrollment from firing without ending any of
     * them; resuming picks them up where they stood.
     */
    public Sequence update(String sequenceId, BroadcastParams.UpdateSequence params) {
        return http.convert(
                ApiClient.unwrap(
                        http.request("PATCH", "/v1/sequences/" + sequenceId, params.toBody(), null)),
                Sequence.class);
    }

    /**
     * Put contacts on the sequence, by id or by audience.
     *
     * <p>Re-enrolling someone restarts their walk from the first step rather than running two
     * in parallel. Needs the {@code publish} scope as well as {@code inbox}.
     */
    public Enrolled enroll(String sequenceId, BroadcastParams.Enroll who) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/sequences/" + sequenceId + "/enroll", who.toBody())),
                Enrolled.class);
    }

    /**
     * Take contacts off the sequence. Nothing further fires for them. Needs the
     * {@code publish} scope.
     */
    public Unenrolled unenroll(String sequenceId, List<String> contactIds) {
        return http.convert(
                ApiClient.unwrap(
                        http.post(
                                "/v1/sequences/" + sequenceId + "/unenroll",
                                Map.of("contact_ids", contactIds))),
                Unenrolled.class);
    }

    /** Who is on the sequence, what step they are at, and when the next one is due. */
    public EnrollmentPage enrollments(String sequenceId) {
        return enrollments(sequenceId, BroadcastParams.Enrollments.create());
    }

    /** Who is on the sequence, what step they are at, and when the next one is due. */
    public EnrollmentPage enrollments(String sequenceId, BroadcastParams.Enrollments filter) {
        return http.convert(
                http.get("/v1/sequences/" + sequenceId + "/enrollments", filter.toQuery()),
                EnrollmentPage.class);
    }

    /** Remove a sequence and every enrollment on it. */
    public void delete(String sequenceId) {
        http.delete("/v1/sequences/" + sequenceId);
    }
}
