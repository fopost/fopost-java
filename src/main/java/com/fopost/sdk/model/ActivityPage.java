package com.fopost.sdk.model;

import java.util.Iterator;
import java.util.List;

/**
 * One page of activity, newest first. Pass {@code nextCursor} back as the cursor for the next
 * page; it is {@code null} at the end of the list.
 */
public record ActivityPage(List<ActivityEvent> data, String nextCursor)
        implements Iterable<ActivityEvent> {

    public ActivityPage {
        data = data == null ? List.of() : List.copyOf(data);
    }

    @Override
    public Iterator<ActivityEvent> iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
