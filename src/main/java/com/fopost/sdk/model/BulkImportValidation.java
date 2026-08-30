package com.fopost.sdk.model;

import java.util.List;

/** The dry-run result of a bulk-import CSV: every row, with the reasons any of them cannot be created. */
public record BulkImportValidation(Integer totalRows, Integer validRows, Integer invalidRows, List<Row> rows) {

    public record Row(
            Integer row,
            String contentPreview,
            String scheduleAt,
            List<String> accounts,
            Integer labels,
            Boolean hasMedia,
            List<String> errors) {}
}
