package com.fopost.sdk.model;

import java.util.List;

/**
 * The fonts a design can use.
 *
 * @param library the fonts FoPost ships
 * @param uploaded the workspace's own fonts; empty when no workspace was named
 */
public record FontList(List<LibraryFont> library, List<UploadedFont> uploaded) {}
