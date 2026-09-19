package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.Map;

/** The file a direct upload is being reserved for. {@code size} is the exact byte count that will be sent. */
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

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
