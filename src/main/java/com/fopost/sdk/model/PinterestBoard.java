package com.fopost.sdk.model;

/** A Pinterest board a Pin can land on; pass {@code id} as the {@code board_id} platform setting. */
public record PinterestBoard(String id, String name, String privacy, String description, String image) {}
