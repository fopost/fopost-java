package com.fopost.sdk.model;

import java.util.Iterator;
import java.util.List;

/** One page of broadcasts: its rows plus the pagination block. Iterates over its rows. */
public record BroadcastPage(List<Broadcast> data, BroadcastPageMeta pagination)
        implements Iterable<Broadcast> {

    public BroadcastPage {
        data = data == null ? List.of() : List.copyOf(data);
    }

    @Override
    public Iterator<Broadcast> iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
