package com.fopost.sdk.model;

import java.util.List;

/**
 * A column the workspace invented to keep about its contacts.
 *
 * <p>{@code key} is the machine name and the CSV column header, fixed once created.
 * {@code type} is {@code text}, {@code number}, {@code date}, {@code select} or
 * {@code boolean}; {@code options} carries the allowed values when it is {@code select}.
 */
public record ContactField(String id, String key, String name, String type, List<String> options, int position) {

    public ContactField {
        options = options == null ? List.of() : List.copyOf(options);
    }
}
