package com.fopost.sdk.model;

import java.util.Iterator;
import java.util.List;

/** One page of an inbox list: its items plus the pagination meta. Iterates over its items. */
public record InboxPage<T>(List<T> data, InboxPageMeta meta) implements Iterable<T> {

    public InboxPage {
        data = data == null ? List.of() : List.copyOf(data);
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
