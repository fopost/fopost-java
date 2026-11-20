package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.internal.Multipart;
import com.fopost.sdk.model.FontList;
import com.fopost.sdk.model.UploadedFont;
import com.fopost.sdk.param.FileInput;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The font library and a workspace's uploaded fonts, for designs. Needs the {@code posts} scope and
 * create access on the workspace.
 *
 * <p>A font file is at most 5 MB; a larger one answers {@code 413} with {@code file_too_large}, and
 * a workspace at its font cap answers {@code 409} with {@code font_limit_reached}.
 */
public final class FontsResource {

    private final ApiClient http;

    public FontsResource(ApiClient http) {
        this.http = http;
    }

    /** The library only. */
    public FontList list() {
        return list(null);
    }

    /** The library plus the workspace's uploaded fonts. */
    public FontList list(String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        if (workspaceId != null) {
            query.put("workspace_id", workspaceId);
        }
        return http.convert(http.get("/v1/fonts", query), FontList.class);
    }

    public UploadedFont upload(String workspaceId, Path file) {
        return upload(workspaceId, FileInput.of(file), null, null, null);
    }

    public UploadedFont upload(String workspaceId, String filename, byte[] content, String contentType) {
        return upload(workspaceId, FileInput.of(filename, contentType, content), null, null, null);
    }

    /**
     * Uploads one TTF, OTF, WOFF or WOFF2 file. {@code family} defaults to the file name,
     * {@code weight} (100 to 1000) to 400, and {@code style} ({@code normal} or {@code italic}) to
     * {@code normal}; pass null for any of them.
     */
    public UploadedFont upload(String workspaceId, FileInput file, String family, Integer weight, String style) {
        Multipart body = new Multipart()
                .file("file", file.filename(), file.mimeType(), file.source(), file.size())
                .field("workspace_id", workspaceId);
        if (family != null) {
            body.field("family", family);
        }
        if (weight != null) {
            body.field("weight", String.valueOf(weight));
        }
        if (style != null) {
            body.field("style", style);
        }
        return http.convert(ApiClient.unwrap(http.request("POST", "/v1/fonts", body, null)), UploadedFont.class);
    }

    public void delete(String fontId) {
        http.delete("/v1/fonts/" + fontId);
    }
}
