package com.fopost.sdk.model;

import java.util.List;

/** The persistent menu set on one account, one entry per locale. */
public record MetaPersistentMenu(List<MetaPersistentMenuEntry> persistentMenu) {}
