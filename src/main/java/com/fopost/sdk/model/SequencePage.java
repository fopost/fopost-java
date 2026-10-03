package com.fopost.sdk.model;

import java.util.Iterator;
import java.util.List;

/** One page of sequences. Iterates over its rows. */
public record SequencePage(List<Sequence> data, BroadcastPageMeta pagination)
        implements Iterable<Sequence> {

    public SequencePage {
        data = data == null ? List.of() : List.copyOf(data);
    }

    @Override
    public Iterator<Sequence> iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
