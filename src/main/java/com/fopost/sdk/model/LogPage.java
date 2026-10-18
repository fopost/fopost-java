package com.fopost.sdk.model;

import java.util.Iterator;
import java.util.List;

/**
 * One page of the request log, newest first. Pass {@code nextCursor} back as
 * {@link com.fopost.sdk.param.LogParams#before} for the next page; it is {@code null} at the end.
 */
public record LogPage(List<LogEntry> data, String nextCursor, Boolean hasMore, LogSummary summary)
        implements Iterable<LogEntry> {

    public LogPage {
        data = data == null ? List.of() : List.copyOf(data);
    }

    @Override
    public Iterator<LogEntry> iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
