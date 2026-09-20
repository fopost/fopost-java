package com.fopost.sdk.model;

import java.util.List;

/** One page of ad-library results; pass {@code nextCursor} back as the cursor. */
public record AdLibraryPage(List<AdLibraryAd> ads, String nextCursor) {}
