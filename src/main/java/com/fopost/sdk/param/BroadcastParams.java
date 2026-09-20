package com.fopost.sdk.param;

import com.fopost.sdk.model.AudienceFilter;
import com.fopost.sdk.model.SequenceStep;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Filters and bodies for {@code broadcasts()} and {@code sequences()}.
 *
 * <p>Each builder sends only what was set, so a patch stays partial.
 */
public final class BroadcastParams {

    private BroadcastParams() {}

    /** Filters for the broadcasts listing. */
    public static final class Filter {

        private final Map<String, Object> query = new LinkedHashMap<>();

        public static Filter create() {
            return new Filter();
        }

        /** Omit to span every workspace the key can reach. */
        public Filter workspace(String workspaceId) {
            Params.put(query, "workspace_id", workspaceId);
            return this;
        }

        /** {@code draft}, {@code scheduled}, {@code sending}, {@code sent} or {@code cancelled}. */
        public Filter status(String status) {
            Params.put(query, "status", status);
            return this;
        }

        public Filter page(int page) {
            Params.put(query, "page", page);
            return this;
        }

        public Filter perPage(int perPage) {
            Params.put(query, "per_page", perPage);
            return this;
        }

        public Map<String, Object> toQuery() {
            return new LinkedHashMap<>(query);
        }
    }

    /** Filters for a broadcast's recipients listing. */
    public static final class Recipients {

        private final Map<String, Object> query = new LinkedHashMap<>();

        public static Recipients create() {
            return new Recipients();
        }

        /** {@code pending}, {@code sent}, {@code skipped} or {@code failed}. */
        public Recipients status(String status) {
            Params.put(query, "status", status);
            return this;
        }

        public Recipients page(int page) {
            Params.put(query, "page", page);
            return this;
        }

        public Recipients perPage(int perPage) {
            Params.put(query, "per_page", perPage);
            return this;
        }

        public Map<String, Object> toQuery() {
            return new LinkedHashMap<>(query);
        }
    }

    /** The body of a broadcast create. Creating never sends. */
    public static final class Create {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static Create of(String workspaceId, String accountId, String name, String text) {
            Create create = new Create();
            create.body.put("workspace_id", workspaceId);
            create.body.put("account_id", accountId);
            create.body.put("name", name);
            create.body.put("text", text);
            return create;
        }

        public Create mediaId(String mediaId) {
            Params.put(body, "media_id", mediaId);
            return this;
        }

        /** Omit to reach every contact in the workspace. */
        public Create audience(AudienceFilter audience) {
            Params.put(body, "audience", audience);
            return this;
        }

        /** Send it at this time instead of on demand. */
        public Create scheduledAt(String scheduledAt) {
            Params.put(body, "scheduled_at", scheduledAt);
            return this;
        }

        public Map<String, Object> toBody() {
            return new LinkedHashMap<>(body);
        }
    }

    /** The body of a broadcast patch. Only a draft or scheduled broadcast can be edited. */
    public static final class Update {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static Update create() {
            return new Update();
        }

        public Update name(String name) {
            Params.put(body, "name", name);
            return this;
        }

        public Update text(String text) {
            Params.put(body, "text", text);
            return this;
        }

        public Update mediaId(String mediaId) {
            body.put("media_id", mediaId);
            return this;
        }

        public Update audience(AudienceFilter audience) {
            Params.put(body, "audience", audience);
            return this;
        }

        public Update scheduledAt(String scheduledAt) {
            body.put("scheduled_at", scheduledAt);
            return this;
        }

        public Map<String, Object> toBody() {
            return new LinkedHashMap<>(body);
        }
    }

    /** Filters for the sequences listing. */
    public static final class SequenceFilter {

        private final Map<String, Object> query = new LinkedHashMap<>();

        public static SequenceFilter create() {
            return new SequenceFilter();
        }

        public SequenceFilter workspace(String workspaceId) {
            Params.put(query, "workspace_id", workspaceId);
            return this;
        }

        public SequenceFilter page(int page) {
            Params.put(query, "page", page);
            return this;
        }

        public SequenceFilter perPage(int perPage) {
            Params.put(query, "per_page", perPage);
            return this;
        }

        public Map<String, Object> toQuery() {
            return new LinkedHashMap<>(query);
        }
    }

    /** The body of a sequence create. Creating one enrolls nobody. */
    public static final class CreateSequence {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static CreateSequence of(
                String workspaceId, String accountId, String name, List<SequenceStep> steps) {
            CreateSequence create = new CreateSequence();
            create.body.put("workspace_id", workspaceId);
            create.body.put("account_id", accountId);
            create.body.put("name", name);
            create.body.put("steps", steps);
            return create;
        }

        /** {@code active} (the default) or {@code paused}. */
        public CreateSequence status(String status) {
            Params.put(body, "status", status);
            return this;
        }

        public Map<String, Object> toBody() {
            return new LinkedHashMap<>(body);
        }
    }

    /**
     * The body of a sequence patch.
     *
     * <p>Pausing stops every enrollment from firing without ending any of them; resuming
     * picks them up where they stood.
     */
    public static final class UpdateSequence {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static UpdateSequence create() {
            return new UpdateSequence();
        }

        public UpdateSequence name(String name) {
            Params.put(body, "name", name);
            return this;
        }

        public UpdateSequence steps(List<SequenceStep> steps) {
            Params.put(body, "steps", steps);
            return this;
        }

        public UpdateSequence status(String status) {
            Params.put(body, "status", status);
            return this;
        }

        public Map<String, Object> toBody() {
            return new LinkedHashMap<>(body);
        }
    }

    /** Who to enroll: named contacts, or the audience they are drawn from. */
    public static final class Enroll {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static Enroll contacts(List<String> contactIds) {
            Enroll enroll = new Enroll();
            enroll.body.put("contact_ids", contactIds);
            return enroll;
        }

        public static Enroll audience(AudienceFilter audience) {
            Enroll enroll = new Enroll();
            enroll.body.put("audience", audience);
            return enroll;
        }

        public Map<String, Object> toBody() {
            return new LinkedHashMap<>(body);
        }
    }

    /** Pagination for a sequence's enrollments listing. */
    public static final class Enrollments {

        private final Map<String, Object> query = new LinkedHashMap<>();

        public static Enrollments create() {
            return new Enrollments();
        }

        public Enrollments page(int page) {
            Params.put(query, "page", page);
            return this;
        }

        public Enrollments perPage(int perPage) {
            Params.put(query, "per_page", perPage);
            return this;
        }

        public Map<String, Object> toQuery() {
            return new LinkedHashMap<>(query);
        }
    }
}
