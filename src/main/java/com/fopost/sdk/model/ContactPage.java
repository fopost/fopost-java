package com.fopost.sdk.model;

import java.util.Iterator;
import java.util.List;

/** One page of contacts: its rows plus the pagination block. Iterates over its rows. */
public record ContactPage(List<Contact> data, ContactPageMeta pagination) implements Iterable<Contact> {

    public ContactPage {
        data = data == null ? List.of() : List.copyOf(data);
    }

    @Override
    public Iterator<Contact> iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
