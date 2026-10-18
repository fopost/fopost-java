package com.fopost.sdk.model;

import java.util.Iterator;
import java.util.List;

/** One page of an X search: the posts plus the cursor for the next page. Iterates over its posts. */
public record XSearchPage(List<XPost> data, XSearchPageMeta meta) implements Iterable<XPost> {

    public XSearchPage {
        data = data == null ? List.of() : List.copyOf(data);
    }

    @Override
    public Iterator<XPost> iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
