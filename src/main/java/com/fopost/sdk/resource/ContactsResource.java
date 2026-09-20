package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Contact;
import com.fopost.sdk.model.ContactConversation;
import com.fopost.sdk.model.ContactField;
import com.fopost.sdk.model.ContactImportResult;
import com.fopost.sdk.model.ContactPage;
import com.fopost.sdk.model.ConversationAnalytics;
import com.fopost.sdk.param.ContactParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The people behind the inbox, and the fields a workspace keeps about them.
 *
 * <p>A contact is one person however many handles they write from. An inbound inbox item
 * files its author, a reply files whoever you answered, and both fold into whatever is
 * already on file, so the same person never becomes two rows.
 *
 * <p>Every method needs the {@code inbox} scope, except {@link #conversationAnalytics},
 * which needs {@code analytics}.
 */
public final class ContactsResource {

    private final ApiClient http;

    public ContactsResource(ApiClient http) {
        this.http = http;
    }

    /** One page of contacts, most recently active first. */
    public ContactPage list() {
        return list(ContactParams.Filter.create());
    }

    /**
     * One page of contacts, most recently active first.
     *
     * <p>Omit the workspace to span every workspace the key can reach; each contact then
     * carries {@code workspaceId}.
     */
    public ContactPage list(ContactParams.Filter filter) {
        return http.convert(http.get("/v1/contacts", filter.toQuery()), ContactPage.class);
    }

    /**
     * One contact. A contact in a workspace the key cannot reach answers 404, exactly as an
     * id that never existed does.
     */
    public Contact get(String contactId) {
        return http.convert(ApiClient.unwrap(http.get("/v1/contacts/" + contactId, null)), Contact.class);
    }

    /**
     * File a contact.
     *
     * <p>It folds into the contact that already holds the first channel, so this cannot
     * duplicate someone the inbox has already met.
     */
    public Contact create(ContactParams.Create params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/contacts", params.toBody())), Contact.class);
    }

    /** Patch a contact. Only what the builder set is sent. */
    public Contact update(String contactId, ContactParams.Update params) {
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", "/v1/contacts/" + contactId, params.toBody(), null)),
                Contact.class);
    }

    /**
     * Remove a contact and its field values. The messages they sent stay in the inbox, so a
     * later message files them again.
     */
    public void delete(String contactId) {
        http.delete("/v1/contacts/" + contactId);
    }

    /** The threads one contact appears in, newest first. */
    public List<ContactConversation> conversations(String contactId) {
        return conversations(contactId, 0);
    }

    /**
     * The threads one contact appears in, newest first. Matched on their channels, so a
     * contact merged from two handles brings both threads with it. A limit of zero leaves
     * the server default.
     */
    public List<ContactConversation> conversations(String contactId, int limit) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (limit > 0) {
            query.put("limit", limit);
        }
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/contacts/" + contactId + "/conversations", query)),
                ContactConversation.class);
    }

    /**
     * Import contacts from CSV text.
     *
     * <p>{@code platform} and {@code handle} are required columns; any other column is read
     * as a custom field key, and one matching no field comes back in {@code unknownColumns}
     * rather than being stored.
     */
    public ContactImportResult importCsv(String workspaceId, String csv) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspace_id", workspaceId);
        body.put("csv", csv);
        return http.convert(
                ApiClient.unwrap(http.post("/v1/contacts/import", body)), ContactImportResult.class);
    }

    /** The columns this workspace keeps about its contacts, in display order. */
    public List<ContactField> listFields(String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("workspace_id", workspaceId);
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/contacts/fields", query)), ContactField.class);
    }

    /**
     * Add a custom field. {@code type} is {@code text}, {@code number}, {@code date},
     * {@code select} or {@code boolean}; options are required for {@code select}. A
     * duplicate key answers 409.
     */
    public ContactField createField(
            String workspaceId, String key, String name, String type, List<String> options) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("workspace_id", workspaceId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("key", key);
        body.put("name", name);
        body.put("type", type);
        body.put("options", options == null ? List.of() : options);
        return http.convert(
                ApiClient.unwrap(http.post("/v1/contacts/fields", body, query)), ContactField.class);
    }

    /** Rename a field. The key and the type are fixed once created. */
    public ContactField updateField(String fieldId, String name) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", "/v1/contacts/fields/" + fieldId, body, null)),
                ContactField.class);
    }

    /** Replace a field's allowed values. */
    public ContactField updateFieldOptions(String fieldId, List<String> options) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("options", options == null ? List.of() : options);
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", "/v1/contacts/fields/" + fieldId, body, null)),
                ContactField.class);
    }

    /** Remove the field and every answer to it. */
    public void deleteField(String fieldId) {
        http.delete("/v1/contacts/fields/" + fieldId);
    }

    /**
     * Volume and median reply time per thread. Counts and timings only: no message text and
     * no author. Needs the {@code analytics} scope rather than {@code inbox}.
     */
    public ConversationAnalytics conversationAnalytics(ContactParams.Conversations params) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/analytics/inbox/conversations", params.toQuery())),
                ConversationAnalytics.class);
    }
}
