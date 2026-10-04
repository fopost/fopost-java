package com.fopost.sdk.model;

/**
 * One handle on one network.
 *
 * <p>{@code handle} is lower-cased with no leading {@code @}. {@code externalId} is the
 * platform's own id for this person when the network gave us one, and it is what a merge
 * prefers: a handle can be changed, an id cannot.
 */
public record ContactChannel(String platform, String handle, String externalId) {

    /** A channel without a platform id, which is all most writes need. */
    public static ContactChannel of(String platform, String handle) {
        return new ContactChannel(platform, handle, null);
    }
}
