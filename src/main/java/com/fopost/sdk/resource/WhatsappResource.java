package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.WhatsappBlockResult;
import com.fopost.sdk.model.WhatsappCommerceSettings;
import com.fopost.sdk.model.WhatsappEncryptionKeyStatus;
import com.fopost.sdk.model.WhatsappFlow;
import com.fopost.sdk.model.WhatsappFlowJsonResult;
import com.fopost.sdk.model.WhatsappFlowResponse;
import com.fopost.sdk.model.WhatsappGroup;
import com.fopost.sdk.model.WhatsappProfile;
import com.fopost.sdk.model.WhatsappSandboxSession;
import com.fopost.sdk.model.WhatsappTemplate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A WhatsApp Business connection: a number the customer already owns.
 *
 * <p>The platform owns templates, flows, the business profile and the commerce
 * settings, so every method here is a live read or write against the customer's
 * own WhatsApp Business Account. Nothing is cached, and all of it answers 503
 * until WhatsApp is set up on the deployment. Every method needs the
 * {@code accounts} scope, except the sandbox, which sends a template and needs
 * {@code publish}.
 */
public final class WhatsappResource {

    private final ApiClient http;

    public WhatsappResource(ApiClient http) {
        this.http = http;
    }

    private String base(String accountId) {
        return "/v1/accounts/" + accountId + "/whatsapp";
    }

    // ─── Profile ──────────────────────────────────────────────────────────────

    public WhatsappProfile getProfile(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.get(base(accountId) + "/profile", null)), WhatsappProfile.class);
    }

    /** A partial update: keys left out of {@code fields} keep their value. */
    public WhatsappProfile updateProfile(String accountId, Map<String, Object> fields) {
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", base(accountId) + "/profile", fields, null)),
                WhatsappProfile.class);
    }

    /** A review, not a write: the number keeps its old name until it passes. */
    public void requestDisplayName(String accountId, String displayName) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("display_name", displayName);
        http.post(base(accountId) + "/profile/display-name", body);
    }

    public WhatsappProfile setUsername(String accountId, String username) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", username);
        return http.convert(
                ApiClient.unwrap(http.put(base(accountId) + "/profile/username", body)),
                WhatsappProfile.class);
    }

    // ─── Templates ────────────────────────────────────────────────────────────

    public List<WhatsappTemplate> listTemplates(String accountId) {
        return listTemplates(accountId, null);
    }

    public List<WhatsappTemplate> listTemplates(String accountId, String after) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (after != null) {
            query.put("after", after);
        }
        return http.convertList(
                ApiClient.unwrap(http.get(base(accountId) + "/templates", query)), WhatsappTemplate.class);
    }

    /** The pre-written templates the platform offers, for adapting. */
    public List<Map<String, Object>> listTemplateLibrary(String accountId, String search) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (search != null) {
            query.put("search", search);
        }
        return http.convertList(
                ApiClient.unwrap(http.get(base(accountId) + "/templates/library", query)), Map.class)
                .stream()
                .map(entry -> (Map<String, Object>) entry)
                .toList();
    }

    public WhatsappTemplate getTemplate(String accountId, String templateId) {
        return http.convert(
                ApiClient.unwrap(http.get(base(accountId) + "/templates/" + templateId, null)),
                WhatsappTemplate.class);
    }

    /**
     * Files a template for review. The result carries the status the platform
     * assigned, which is {@code PENDING} on a normal submission.
     */
    public WhatsappTemplate createTemplate(
            String accountId,
            String name,
            String language,
            String category,
            List<Map<String, Object>> components) {
        return createTemplate(accountId, name, language, category, components, null);
    }

    public WhatsappTemplate createTemplate(
            String accountId,
            String name,
            String language,
            String category,
            List<Map<String, Object>> components,
            Boolean allowCategoryChange) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        body.put("language", language);
        body.put("category", category);
        body.put("components", components);
        if (allowCategoryChange != null) {
            body.put("allow_category_change", allowCategoryChange);
        }
        return http.convert(
                ApiClient.unwrap(http.post(base(accountId) + "/templates", body)), WhatsappTemplate.class);
    }

    /** Creates a template from one of the platform's library entries. */
    public WhatsappTemplate importTemplate(
            String accountId,
            String libraryTemplateName,
            String name,
            String language,
            String category,
            List<Map<String, Object>> libraryTemplateButtonInputs) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("library_template_name", libraryTemplateName);
        body.put("name", name);
        body.put("language", language);
        body.put("category", category);
        if (libraryTemplateButtonInputs != null) {
            body.put("library_template_button_inputs", libraryTemplateButtonInputs);
        }
        return http.convert(
                ApiClient.unwrap(http.post(base(accountId) + "/templates/import", body)),
                WhatsappTemplate.class);
    }

    /** Edits a template. The name cannot change; create a new one instead. */
    public WhatsappTemplate updateTemplate(
            String accountId, String templateId, String category, List<Map<String, Object>> components) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (category != null) {
            body.put("category", category);
        }
        if (components != null) {
            body.put("components", components);
        }
        return http.convert(
                ApiClient.unwrap(
                        http.request("PATCH", base(accountId) + "/templates/" + templateId, body, null)),
                WhatsappTemplate.class);
    }

    /** The name is required: it is what the platform deletes by. */
    public void deleteTemplate(String accountId, String templateId, String name) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("name", name);
        http.request("DELETE", base(accountId) + "/templates/" + templateId, null, query);
    }

    // ─── Groups ───────────────────────────────────────────────────────────────

    public List<WhatsappGroup> listGroups(String accountId) {
        return http.convertList(
                ApiClient.unwrap(http.get(base(accountId) + "/groups", null)), WhatsappGroup.class);
    }

    /** Participation is invite-only: send the invite link, there is no add. */
    public WhatsappGroup createGroup(String accountId, String subject, String description) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("subject", subject);
        if (description != null) {
            body.put("description", description);
        }
        return http.convert(
                ApiClient.unwrap(http.post(base(accountId) + "/groups", body)), WhatsappGroup.class);
    }

    public WhatsappGroup getGroup(String accountId, String groupId) {
        return http.convert(
                ApiClient.unwrap(http.get(base(accountId) + "/groups/" + groupId, null)),
                WhatsappGroup.class);
    }

    public WhatsappGroup updateGroup(
            String accountId, String groupId, String subject, String description) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (subject != null) {
            body.put("subject", subject);
        }
        if (description != null) {
            body.put("description", description);
        }
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", base(accountId) + "/groups/" + groupId, body, null)),
                WhatsappGroup.class);
    }

    public void deleteGroup(String accountId, String groupId) {
        http.delete(base(accountId) + "/groups/" + groupId);
    }

    /** The link someone joins the group with. */
    public String getGroupInviteLink(String accountId, String groupId) {
        return inviteLinkOf(
                http.get(base(accountId) + "/groups/" + groupId + "/invite-link", null));
    }

    /** Issues a new link and invalidates the old one. */
    public String resetGroupInviteLink(String accountId, String groupId) {
        return inviteLinkOf(http.post(base(accountId) + "/groups/" + groupId + "/invite-link", null));
    }

    private String inviteLinkOf(com.fasterxml.jackson.databind.JsonNode body) {
        var link = ApiClient.unwrap(body).get("inviteLink");
        return link == null || link.isNull() ? null : link.asText();
    }

    public void removeGroupParticipants(String accountId, String groupId, List<String> users) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("users", users);
        http.request("DELETE", base(accountId) + "/groups/" + groupId + "/participants", body, null);
    }

    // ─── Blocking ─────────────────────────────────────────────────────────────

    public List<String> listBlocked(String accountId, String after) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (after != null) {
            query.put("after", after);
        }
        return http.convertList(ApiClient.unwrap(http.get(base(accountId) + "/block", query)), String.class);
    }

    public WhatsappBlockResult blockUsers(String accountId, List<String> users) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("users", users);
        return http.convert(
                ApiClient.unwrap(http.post(base(accountId) + "/block", body)), WhatsappBlockResult.class);
    }

    public WhatsappBlockResult unblockUsers(String accountId, List<String> users) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("users", users);
        return http.convert(
                ApiClient.unwrap(http.request("DELETE", base(accountId) + "/block", body, null)),
                WhatsappBlockResult.class);
    }

    // ─── Commerce ─────────────────────────────────────────────────────────────

    public WhatsappCommerceSettings getCommerceSettings(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.get(base(accountId) + "/commerce", null)),
                WhatsappCommerceSettings.class);
    }

    public WhatsappCommerceSettings updateCommerceSettings(
            String accountId, Boolean cartEnabled, Boolean catalogVisible) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (cartEnabled != null) {
            body.put("is_cart_enabled", cartEnabled);
        }
        if (catalogVisible != null) {
            body.put("is_catalog_visible", catalogVisible);
        }
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", base(accountId) + "/commerce", body, null)),
                WhatsappCommerceSettings.class);
    }

    /** Points the number at a catalog the customer already owns. */
    public WhatsappCommerceSettings linkCatalog(String accountId, String catalogId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("catalog_id", catalogId);
        return http.convert(
                ApiClient.unwrap(http.post(base(accountId) + "/commerce/catalog", body)),
                WhatsappCommerceSettings.class);
    }

    // ─── Flows ────────────────────────────────────────────────────────────────

    public List<WhatsappFlow> listFlows(String accountId) {
        return http.convertList(
                ApiClient.unwrap(http.get(base(accountId) + "/flows", null)), WhatsappFlow.class);
    }

    public WhatsappFlow getFlow(String accountId, String flowId) {
        return http.convert(
                ApiClient.unwrap(http.get(base(accountId) + "/flows/" + flowId, null)), WhatsappFlow.class);
    }

    /** Creates a draft flow; its screens are uploaded separately. */
    public WhatsappFlow createFlow(String accountId, String name, List<String> categories) {
        return createFlow(accountId, name, categories, null, null);
    }

    public WhatsappFlow createFlow(
            String accountId, String name, List<String> categories, String endpointUri, String cloneFlowId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        body.put("categories", categories);
        if (endpointUri != null) {
            body.put("endpoint_uri", endpointUri);
        }
        if (cloneFlowId != null) {
            body.put("clone_flow_id", cloneFlowId);
        }
        return http.convert(
                ApiClient.unwrap(http.post(base(accountId) + "/flows", body)), WhatsappFlow.class);
    }

    public WhatsappFlow updateFlow(
            String accountId, String flowId, String name, List<String> categories, String endpointUri) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (name != null) {
            body.put("name", name);
        }
        if (categories != null) {
            body.put("categories", categories);
        }
        if (endpointUri != null) {
            body.put("endpoint_uri", endpointUri);
        }
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", base(accountId) + "/flows/" + flowId, body, null)),
                WhatsappFlow.class);
    }

    /** Drafts only; a published flow is deprecated instead. */
    public void deleteFlow(String accountId, String flowId) {
        http.delete(base(accountId) + "/flows/" + flowId);
    }

    /**
     * Replaces the flow's screens. The platform answers with its validation
     * errors rather than refusing, so they come back as data.
     */
    public WhatsappFlowJsonResult uploadFlowJson(
            String accountId, String flowId, Map<String, Object> flowJson) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("flow_json", flowJson);
        return http.convert(
                ApiClient.unwrap(http.put(base(accountId) + "/flows/" + flowId + "/json", body)),
                WhatsappFlowJsonResult.class);
    }

    public WhatsappFlow publishFlow(String accountId, String flowId) {
        return http.convert(
                ApiClient.unwrap(http.post(base(accountId) + "/flows/" + flowId + "/publish", null)),
                WhatsappFlow.class);
    }

    public WhatsappFlow deprecateFlow(String accountId, String flowId) {
        return http.convert(
                ApiClient.unwrap(http.post(base(accountId) + "/flows/" + flowId + "/deprecate", null)),
                WhatsappFlow.class);
    }

    public List<WhatsappFlowResponse> listFlowResponses(String accountId) {
        return http.convertList(
                ApiClient.unwrap(http.get(base(accountId) + "/flows/responses", null)),
                WhatsappFlowResponse.class);
    }

    public WhatsappEncryptionKeyStatus getEncryptionKeyStatus(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.get(base(accountId) + "/flows/encryption-key", null)),
                WhatsappEncryptionKeyStatus.class);
    }

    /**
     * Registers the public half of the key the platform encrypts a flow
     * endpoint's payloads with. The private half stays with the customer.
     */
    public WhatsappEncryptionKeyStatus setEncryptionKey(String accountId, String businessPublicKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("business_public_key", businessPublicKey);
        return http.convert(
                ApiClient.unwrap(http.put(base(accountId) + "/flows/encryption-key", body)),
                WhatsappEncryptionKeyStatus.class);
    }

    // ─── Account state and sandbox ────────────────────────────────────────────

    /** The account review state and the number's quality and limit tier. */
    public Map<String, Object> getAccountEvents(String accountId) {
        return http.convert(ApiClient.unwrap(http.get(base(accountId) + "/events", null)), Map.class);
    }

    public List<WhatsappSandboxSession> listSandboxSessions(String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("workspaceId", workspaceId);
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/whatsapp/sandbox/sessions", query)),
                WhatsappSandboxSession.class);
    }

    /**
     * Invites one tester to the platform-owned test number. Inviting sends a
     * template, so it needs the {@code publish} scope.
     */
    public WhatsappSandboxSession createSandboxSession(String workspaceId, String phoneNumber) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspaceId", workspaceId);
        body.put("phoneNumber", phoneNumber);
        return http.convert(
                ApiClient.unwrap(http.post("/v1/whatsapp/sandbox/sessions", body)),
                WhatsappSandboxSession.class);
    }
}
