package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.internal.Multipart;
import com.fopost.sdk.model.MediaLibraryItem;
import com.fopost.sdk.model.PresignedUpload;
import com.fopost.sdk.model.UploadedMedia;
import com.fopost.sdk.param.PresignUploadParams;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The workspace media library. Upload once, then attach the returned url to a content block. */
public final class MediaResource {

    private final ApiClient http;

    public MediaResource(ApiClient http) {
        this.http = http;
    }

    public List<MediaLibraryItem> list(String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("workspaceId", workspaceId);
        return http.convertList(ApiClient.unwrap(http.get("/v1/media", query)), MediaLibraryItem.class);
    }

    /** Upload one or more files. Each is checked against the plan's storage allowance. */
    public List<UploadedMedia> upload(String workspaceId, Path... files) {
        Multipart body = new Multipart();
        for (Path file : files) {
            body.file("files", file.getFileName().toString(), contentType(file), read(file));
        }
        body.field("workspaceId", workspaceId);
        return http.convertList(
                ApiClient.unwrap(http.request("POST", "/v1/media/upload", body, null)), UploadedMedia.class);
    }

    public UploadedMedia upload(String workspaceId, String filename, byte[] content, String contentType) {
        Multipart body = new Multipart()
                .file("files", filename, contentType, content)
                .field("workspaceId", workspaceId);
        List<UploadedMedia> uploaded = http.convertList(
                ApiClient.unwrap(http.request("POST", "/v1/media/upload", body, null)), UploadedMedia.class);
        return uploaded.isEmpty() ? null : uploaded.get(0);
    }

    /** Reserve a direct upload. The bytes then go straight to {@link PresignedUpload#uploadUrl()}. */
    public PresignedUpload presign(PresignUploadParams params) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/media/presign", params.toMap())), PresignedUpload.class);
    }

    /** Finish a direct upload once the bytes are at the upload URL, adding the file to the library. */
    public UploadedMedia complete(String uploadId) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/media/presign/" + uploadId + "/complete", null)),
                UploadedMedia.class);
    }

    /** Presign, send the bytes to the upload URL with the returned headers, then complete. */
    public UploadedMedia uploadDirect(String workspaceId, String filename, String mimeType, byte[] data) {
        PresignedUpload presigned = presign(PresignUploadParams.of(workspaceId, filename, mimeType, data.length));
        http.sendRaw(presigned.method(), presigned.uploadUrl(), presigned.headers(), data);
        return complete(presigned.uploadId());
    }

    /** Removes the file from the library. Posts already published keep the copy on the platform. */
    public void delete(String mediaId) {
        http.delete("/v1/media/" + mediaId);
    }

    private static String contentType(Path file) {
        String guessed = URLConnection.guessContentTypeFromName(file.getFileName().toString());
        return guessed == null ? "application/octet-stream" : guessed;
    }

    private static byte[] read(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
