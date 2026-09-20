package com.fopost.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Who a broadcast or an enrollment resolves to, expressed over contacts.
 *
 * <p>Every clause narrows: a contact has to match all of them. A null clause is not sent.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AudienceFilter(
        List<String> platforms, List<String> labelIds, String source, List<AudienceField> fields) {

    /** Everyone in the workspace. */
    public static AudienceFilter all() {
        return new AudienceFilter(null, null, null, null);
    }

    /** Contacts with a handle on at least one of these networks. */
    public AudienceFilter platforms(String... platforms) {
        return new AudienceFilter(List.of(platforms), labelIds, source, fields);
    }

    public AudienceFilter labelIds(String... ids) {
        return new AudienceFilter(platforms, List.of(ids), source, fields);
    }

    /** {@code inbox}, {@code radar} or {@code import}. */
    public AudienceFilter source(String source) {
        return new AudienceFilter(platforms, labelIds, source, fields);
    }

    public AudienceFilter fields(List<AudienceField> fields) {
        return new AudienceFilter(platforms, labelIds, source, fields);
    }
}
