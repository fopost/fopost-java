package com.fopost.sdk.model;

import java.util.Iterator;
import java.util.List;

/** One page of a broadcast's recipients. Iterates over its rows. */
public record RecipientPage(List<BroadcastRecipient> data, BroadcastPageMeta pagination)
        implements Iterable<BroadcastRecipient> {

    public RecipientPage {
        data = data == null ? List.of() : List.copyOf(data);
    }

    @Override
    public Iterator<BroadcastRecipient> iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
