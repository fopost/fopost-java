package com.fopost.sdk.model;

import java.util.List;

/**
 * What a CSV import did.
 *
 * <p>{@code merged} counts rows that folded into a contact already on file.
 * {@code unknownColumns} names columns matching neither a reserved field nor a custom
 * field; they are reported, never stored.
 */
public record ContactImportResult(
        int created, int merged, List<ContactImportSkip> skipped, List<String> unknownColumns) {

    public ContactImportResult {
        skipped = skipped == null ? List.of() : List.copyOf(skipped);
        unknownColumns = unknownColumns == null ? List.of() : List.copyOf(unknownColumns);
    }
}
