package com.fopost.sdk.model;

import java.util.List;

/**
 * Every platform the API can publish to.
 *
 * <p>Model fields stay plain {@link String}, so a platform added server-side still parses on an
 * older SDK. These constants are a convenience for callers, never a validation gate.
 */
public final class Platforms {

    private Platforms() {}

    public static final String TWITTER = "twitter";
    public static final String LINKEDIN = "linkedin";
    public static final String FACEBOOK = "facebook";
    public static final String INSTAGRAM = "instagram";
    public static final String INSTAGRAM_BUSINESS = "instagram-business";
    public static final String TELEGRAM = "telegram";
    public static final String TWITCH = "twitch";
    public static final String DISCORD = "discord";
    public static final String SLACK = "slack";
    public static final String REDDIT = "reddit";
    public static final String PINTEREST = "pinterest";
    public static final String SNAPCHAT = "snapchat";
    public static final String TUMBLR = "tumblr";
    public static final String DRIBBBLE = "dribbble";
    public static final String MEWE = "mewe";
    public static final String TIKTOK = "tiktok";
    public static final String YOUTUBE = "youtube";
    public static final String BLUESKY = "bluesky";
    public static final String THREADS = "threads";
    public static final String MASTODON = "mastodon";
    public static final String LEMMY = "lemmy";
    public static final String DEVTO = "devto";
    public static final String HASHNODE = "hashnode";
    public static final String MEDIUM = "medium";
    public static final String SUBSTACK = "substack";
    public static final String GOOGLE_BUSINESS = "google-business";
    public static final String KICK = "kick";
    public static final String LISTMONK = "listmonk";
    public static final String WORDPRESS = "wordpress";
    public static final String NOSTR = "nostr";
    public static final String WHOP = "whop";
    public static final String SKOOL = "skool";

    public static final List<String> ALL = List.of(
            TWITTER, LINKEDIN, FACEBOOK, INSTAGRAM, INSTAGRAM_BUSINESS, TELEGRAM, TWITCH, DISCORD,
            SLACK, REDDIT, PINTEREST, SNAPCHAT, TUMBLR, DRIBBBLE, MEWE, TIKTOK, YOUTUBE, BLUESKY, THREADS,
            MASTODON, LEMMY, DEVTO, HASHNODE, MEDIUM, SUBSTACK, GOOGLE_BUSINESS, KICK, LISTMONK,
            WORDPRESS, NOSTR, WHOP, SKOOL);
}
