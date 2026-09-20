package com.fopost.sdk.param;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fopost.sdk.internal.Json;
import com.fopost.sdk.model.ContactChannel;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Filters and bodies for {@code contacts()}.
 *
 * <p>Each builder sends only what was set, so a patch stays partial. A custom field set to
 * {@code null} through {@link Update#clearField} is cleared rather than left alone.
 */
public final class ContactParams {

    private ContactParams() {}

    /** Filters for the contacts listing. */
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

        /** Matches a display name or any of their handles. */
        public Filter search(String search) {
            Params.put(query, "search", search);
            return this;
        }

        public Filter platform(String platform) {
            Params.put(query, "platform", platform);
            return this;
        }

        /** {@code inbox}, {@code radar} or {@code import}. */
        public Filter source(String source) {
            Params.put(query, "source", source);
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

    /** The body of a contact create. */
    public static final class Create {

        private final Map<String, Object> body = new LinkedHashMap<>();

        public static Create of(String workspaceId, List<ContactChannel> channels) {
            Create create = new Create();
            create.body.put("workspace_id", workspaceId);
            create.body.put("channels", channels);
            return create;
        }

        public Create displayName(String displayName) {
            Params.put(body, "display_name", displayName);
            return this;
        }

        public Create note(String note) {
            Params.put(body, "note", note);
            return this;
        }

        public Create fields(Map<String, String> fields) {
            Params.put(body, "fields", fields);
            return this;
        }

        public Map<String, Object> toBody() {
            return new LinkedHashMap<>(body);
        }
    }

    /** The body of a contact patch. Only what is set is sent. */
    public static final class Update {

        private final Map<String, Object> body = new LinkedHashMap<>();
        private final Map<String, String> fields = new LinkedHashMap<>();
        private boolean touchedFields;

        public static Update create() {
            return new Update();
        }

        public Update displayName(String displayName) {
            body.put("display_name", displayName);
            return this;
        }

        public Update channels(List<ContactChannel> channels) {
            Params.put(body, "channels", channels);
            return this;
        }

        public Update note(String note) {
            body.put("note", note);
            return this;
        }

        public Update field(String key, String value) {
            fields.put(key, value);
            touchedFields = true;
            return this;
        }

        /** Clear one custom field. */
        public Update clearField(String key) {
            fields.put(key, null);
            touchedFields = true;
            return this;
        }

        public Map<String, Object> toBody() {
            Map<String, Object> out = new LinkedHashMap<>(body);
            if (touchedFields) {
                // A plain Map would lose its nulls: the mapper is configured
                // NON_NULL, and a cleared field has to reach the API as null.
                ObjectNode node = Json.MAPPER.createObjectNode();
                fields.forEach((key, value) -> {
                    if (value == null) {
                        node.putNull(key);
                    } else {
                        node.put(key, value);
                    }
                });
                out.put("fields", node);
            }
            return out;
        }
    }

    /** Filters for the per-conversation analytics report. */
    public static final class Conversations {

        private final Map<String, Object> query = new LinkedHashMap<>();

        public static Conversations create() {
            return new Conversations();
        }

        public Conversations workspace(String workspaceId) {
            Params.put(query, "workspace_id", workspaceId);
            return this;
        }

        public Conversations account(String accountId) {
            Params.put(query, "accountId", accountId);
            return this;
        }

        /** The reporting period, 1 to 365. Defaults to 7. */
        public Conversations days(int days) {
            Params.put(query, "days", days);
            return this;
        }

        /** {@code volume} (default), {@code slowest} or {@code recent}. */
        public Conversations sort(String sort) {
            Params.put(query, "sort", sort);
            return this;
        }

        public Conversations page(int page) {
            Params.put(query, "page", page);
            return this;
        }

        public Conversations perPage(int perPage) {
            Params.put(query, "per_page", perPage);
            return this;
        }

        public Map<String, Object> toQuery() {
            return new LinkedHashMap<>(query);
        }
    }
}
