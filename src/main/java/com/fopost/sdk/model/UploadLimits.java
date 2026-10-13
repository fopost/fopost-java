package com.fopost.sdk.model;

import java.util.List;

/**
 * What the account may upload. {@code maxFileBytes} is the largest file by any route;
 * {@code multipartMaxFileBytes} is the most one multipart request carries per file, so anything
 * larger goes through a direct upload. {@code source} is {@code accounts}, {@code add_on} or
 * {@code bypass}, and {@code nextTier} is the ceiling more connected accounts would unlock.
 */
public record UploadLimits(
        Long maxFileBytes,
        String source,
        Integer connectedAccounts,
        Boolean largeUploadsEnabled,
        NextTier nextTier,
        Long multipartMaxFileBytes,
        Integer maxFilesPerUpload,
        Boolean directUploadAvailable,
        List<String> allowedTypes) {

    /** {@code upToAccounts} is null on the top tier. */
    public record NextTier(Integer upToAccounts, Long maxFileBytes) {}
}
