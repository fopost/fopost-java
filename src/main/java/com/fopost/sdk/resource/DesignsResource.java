package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.Design;
import com.fopost.sdk.model.DesignDeleted;
import com.fopost.sdk.model.DesignExport;
import com.fopost.sdk.model.DesignPost;
import com.fopost.sdk.model.DesignSaved;
import com.fopost.sdk.model.DesignVersion;
import com.fopost.sdk.model.TextSlides;
import com.fopost.sdk.param.DesignParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Designs from the create suite, their versions and exports.
 *
 * <pre>{@code
 * Design design = client.designs().create(
 *         DesignParams.Create.of(workspaceId, "post", document).name("Launch card"));
 * DesignExport export = client.designs().export(design.id(), DesignParams.Export.of("png"));
 * }</pre>
 *
 * <p>Needs the {@code posts} scope and create access on the workspace. {@link #textToSlides} spends
 * AI, so it also needs {@code ai}; an empty balance answers {@code 402}.
 */
public final class DesignsResource {

    private final ApiClient http;

    public DesignsResource(ApiClient http) {
        this.http = http;
    }

    /** Newest first, without their documents. */
    public List<Design> list(String workspaceId) {
        return list(DesignParams.Filter.of(workspaceId));
    }

    public List<Design> list(DesignParams.Filter filter) {
        return http.convertList(ApiClient.unwrap(http.get("/v1/designs", filter.toQuery())), Design.class);
    }

    public Design create(DesignParams.Create params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/designs", params.toMap())), Design.class);
    }

    public Design get(String designId) {
        return http.convert(ApiClient.unwrap(http.get("/v1/designs/" + designId, null)), Design.class);
    }

    public DesignSaved update(String designId, DesignParams.Update params) {
        return saved(http.patch("/v1/designs/" + designId, params.toMap()));
    }

    public DesignDeleted delete(String designId) {
        return http.convert(ApiClient.unwrap(http.delete("/v1/designs/" + designId)), DesignDeleted.class);
    }

    public List<DesignVersion> versions(String designId) {
        return versions(designId, false);
    }

    /** {@code includeDocument} returns each version's full document too. */
    public List<DesignVersion> versions(String designId, boolean includeDocument) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (includeDocument) {
            query.put("include", "document");
        }
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/designs/" + designId + "/versions", query)),
                DesignVersion.class);
    }

    /** Puts an earlier version's document back and records it as a new version. */
    public DesignSaved restoreVersion(String designId, String versionId) {
        return saved(http.post(
                "/v1/designs/" + designId + "/versions/" + versionId + "/restore",
                new LinkedHashMap<String, Object>()));
    }

    public Design duplicate(String designId) {
        return duplicate(designId, null);
    }

    /** {@code name} may be null, which names the copy {@code <name> (Copy)}. */
    public Design duplicate(String designId, String name) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (name != null) {
            body.put("name", name);
        }
        return http.convert(
                ApiClient.unwrap(http.post("/v1/designs/" + designId + "/duplicate", body)), Design.class);
    }

    /** Renders the design and adds each file to the media library. */
    public DesignExport export(String designId, DesignParams.Export params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/designs/" + designId + "/export", params.toMap())),
                DesignExport.class);
    }

    public List<Design> bulk(String designId, List<Map<String, String>> rows) {
        return bulk(designId, rows, null);
    }

    /**
     * One new design per row, filling text and image layers matched by layer id or name. At most 50
     * rows. {@code nameColumn} names the column each new design is named after; null numbers them.
     */
    public List<Design> bulk(String designId, List<Map<String, String>> rows, String nameColumn) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("rows", rows);
        if (nameColumn != null) {
            body.put("name_column", nameColumn);
        }
        return http.convertList(
                ApiClient.unwrap(http.post("/v1/designs/" + designId + "/bulk", body)), Design.class);
    }

    /** Posts that use an export of this design, newest first. */
    public List<DesignPost> posts(String designId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/designs/" + designId + "/posts", null)), DesignPost.class);
    }

    /** Splits text into carousel slides. Creates nothing; build the design from the result. */
    public TextSlides textToSlides(DesignParams.TextToSlides params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/designs/text-to-slides", params.toMap())), TextSlides.class);
    }

    /** Creates a text post or carousel design from post text. */
    public Design fromPost(DesignParams.FromPost params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/designs/from-post", params.toMap())), Design.class);
    }

    private DesignSaved saved(JsonNode body) {
        return new DesignSaved(
                http.convert(body.get("data"), Design.class), http.convert(body.get("version"), DesignVersion.class));
    }
}
