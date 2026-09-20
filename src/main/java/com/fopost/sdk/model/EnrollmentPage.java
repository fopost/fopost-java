package com.fopost.sdk.model;

import java.util.Iterator;
import java.util.List;

/** One page of a sequence's enrollments. Iterates over its rows. */
public record EnrollmentPage(List<Enrollment> data, BroadcastPageMeta pagination)
        implements Iterable<Enrollment> {

    public EnrollmentPage {
        data = data == null ? List.of() : List.copyOf(data);
    }

    @Override
    public Iterator<Enrollment> iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
