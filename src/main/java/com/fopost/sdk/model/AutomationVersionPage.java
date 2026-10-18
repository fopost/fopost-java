package com.fopost.sdk.model;

import java.util.Iterator;
import java.util.List;

/** One page of an automation's version history, newest first. */
public record AutomationVersionPage(List<AutomationVersion> data, Meta meta) implements Iterable<AutomationVersion> {

    public AutomationVersionPage {
        data = data == null ? List.of() : List.copyOf(data);
    }

    /** {@code totalPages} is at least 1, so an empty history is one empty page. */
    public record Meta(Integer page, Integer perPage, Integer total, Integer totalPages) {}

    @Override
    public Iterator<AutomationVersion> iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
