package com.fopost.sdk.model;

/** The follow state of an X user after {@code followXUser} or {@code unfollowXUser}. */
public record XFollowResult(String accountId, String userId, Boolean following) {}
