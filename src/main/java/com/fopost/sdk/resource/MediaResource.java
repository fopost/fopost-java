package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.internal.Multipart;
import com.fopost.sdk.model.MediaLibraryItem;
import com.fopost.sdk.model.PresignedUpload;
import com.fopost.sdk.model.UploadLimits;
import com.fopost.sdk.model.UploadedMedia;
import com.fopost.sdk.param.FileInput;
import com.fopost.sdk.param.PresignUploadParams;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The workspace media library. Upload once, then attach the returned url to a content block.
 *
 * <p>A multipart {@link #upload} carries at most {@link UploadLimits#multipartMaxFileBytes()} per
 * file; a larger file goes through {@link #uploadDirect}, up to {@link UploadLimits#maxFileBytes()}.
 * Either answers {@code 413} with code {@code file_too_large} past the ceiling, and the body on
 * the exception carries {@code max_file_bytes}.
 */
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

    /** The account's upload ceilings. {@code workspaceId} may be null. */
    public UploadLimits limits(String workspaceId) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("workspaceId", workspaceId);
        return http.convert(ApiClient.unwrap(http.get("/v1/media/limits", query)), UploadLimits.class);
    }

    /** Upload one or more files in one multipart request. Each is checked against the storage allowance. */
    public List<UploadedMedia> upload(String workspaceId, Path... files) {
        Multipart body = new Multipart();
        for (Path file : files) {
            FileInput input = FileInput.of(file);
            body.file("files", input.filename(), input.mimeType(), input.source(), input.size());
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
        return uploadDirect(workspaceId, FileInput.of(filename, mimeType, data));
    }

    /** The same three steps, streaming the file from its source rather than from memory. */
    public UploadedMedia uploadDirect(String workspaceId, FileInput file) {
        return uploadDirect(PresignUploadParams.of(workspaceId, file), file);
    }

    /** As above, with the reservation's optional fields (folder, brand role, consent) set on {@code params}. */
    public UploadedMedia uploadDirect(PresignUploadParams params, FileInput file) {
        PresignedUpload presigned = presign(params);
        http.sendRaw(presigned.method(), presigned.uploadUrl(), presigned.headers(), file.source(), file.size());
        return complete(presigned.uploadId());
    }

    /** Removes the file from the library. Posts already published keep the copy on the platform. */
    public void delete(String mediaId) {
        http.delete("/v1/media/" + mediaId);
    }
}
