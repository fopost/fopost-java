package com.fopost.sdk.model;

import java.util.List;

/**
 * A font family FoPost ships.
 *
 * @param category {@code sans}, {@code serif}, {@code display}, {@code handwriting} or
 *     {@code monospace}
 * @param scripts {@code latin}, {@code arabic}
 */
public record LibraryFont(
        String slug, String family, String category, List<String> scripts, List<String> weights, List<File> files) {

    /** One font file of the family, covering one subset. */
    public record File(String path, String subset, String weight, String style, String unicodeRange) {}
}
