package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The file a direct upload is being reserved for. {@code size} is the exact byte count that will
 * be sent, up to the account's {@code maxFileBytes}.
 *
 * <pre>{@code
 * PresignUploadParams.of(workspaceId, "logo.png", "image/png", size)
 *     .brandRole("logo")
 *     .brandVoiceId(brandId);
 * }</pre>
 */
public final class PresignUploadParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private PresignUploadParams() {}

    public static PresignUploadParams of(String workspaceId, String filename, String mimeType, long size) {
        PresignUploadParams params = new PresignUploadParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("filename", filename);
        params.body.put("mimeType", mimeType);
        params.body.put("size", size);
        return params;
    }

    public static PresignUploadParams of(String workspaceId, FileInput file) {
        return of(workspaceId, file.filename(), file.mimeType(), file.size());
    }

    public PresignUploadParams folderId(String folderId) {
        Params.put(body, "folderId", folderId);
        return this;
    }

    /** {@code chat} marks the file as produced in the agent chat. */
    public PresignUploadParams origin(String origin) {
        Params.put(body, "origin", origin);
        return this;
    }

    /** {@code logo}, {@code product}, {@code person} or {@code background}. {@code person} needs {@link #personConsent}. */
    public PresignUploadParams brandRole(String brandRole) {
        Params.put(body, "brandRole", brandRole);
        return this;
    }

    public PresignUploadParams brandVoiceId(String brandVoiceId) {
        Params.put(body, "brandVoiceId", brandVoiceId);
        return this;
    }

    public PresignUploadParams personConsent(String subject, String statement) {
        Map<String, Object> consent = new LinkedHashMap<>();
        consent.put("subject", subject);
        consent.put("statement", statement);
        body.put("personConsent", consent);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
