package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.InstagramStory;
import com.fopost.sdk.model.LinkedInMention;
import com.fopost.sdk.model.PinterestBoard;
import com.fopost.sdk.model.TikTokCreatorInfo;
import com.fopost.sdk.model.TikTokMusic;
import com.fopost.sdk.model.TikTokVideoSource;
import com.fopost.sdk.model.YouTubePlaylist;
import com.fopost.sdk.param.CreatePinterestBoardParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlatformExtrasTest {

    @Test
    void createPinterestBoardSendsOnlyWhatWasGiven() {
        FakeTransport transport = new FakeTransport()
                .enqueue(201, "{\"data\":{\"id\":\"b1\",\"name\":\"Recipes\",\"privacy\":\"PUBLIC\"}}");

        PinterestBoard board = TestSupport.client(transport)
                .accounts()
                .createPinterestBoard("a1", CreatePinterestBoardParams.of("Recipes"));

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/pinterest/boards", transport.last().url());
        assertEquals("{\"name\":\"Recipes\"}", transport.lastBody());
        assertEquals("b1", board.id());
    }

    @Test
    void setDefaultYouTubePlaylistSendsNullToClearIt() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":{\"playlist_id\":null}}");

        String stored = TestSupport.client(transport).accounts().setDefaultYouTubePlaylist("a1", null);

        assertEquals("PUT", transport.last().method());
        assertEquals("{\"playlist_id\":null}", transport.lastBody());
        assertNull(stored);
    }

    @Test
    void playlistsMarkTheStoredDefault() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[{\"id\":\"PL1\",\"title\":\"Tutorials\",\"is_default\":true}]}");

        List<YouTubePlaylist> playlists = TestSupport.client(transport).accounts().listYouTubePlaylists("a1");

        assertTrue(playlists.get(0).isDefault());
    }

    @Test
    void tiktokCreatorInfoReportsTheAccountsOwnSwitches() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"privacy_level_options":["PUBLIC_TO_EVERYONE"],"comment_disabled":false,
                                 "duet_disabled":true,"stitch_disabled":false,
                                 "max_video_post_duration_sec":600}}""");

        TikTokCreatorInfo info = TestSupport.client(transport).accounts().getTikTokCreatorInfo("a1");

        assertTrue(info.duetDisabled());
        assertFalse(info.stitchDisabled());
        assertEquals(600, info.maxVideoPostDurationSec());
    }

    @Test
    void tiktokMusicSearchPassesTheQueryThrough() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[{\"id\":\"m1\",\"title\":\"Sunrise\",\"author\":\"Kite\"}]}");

        List<TikTokMusic> tracks =
                TestSupport.client(transport).accounts().searchTikTokMusic("a1", "sunrise", 5);

        assertEquals("m1", tracks.get(0).id());
        assertTrue(transport.last().url().contains("q=sunrise"));
        assertTrue(transport.last().url().contains("limit=5"));
    }

    @Test
    void tiktokVideoLookupReturnsTheAddressARepurposeRunReads() {
        FakeTransport transport = new FakeTransport()
                .enqueue(
                        200,
                        "{\"data\":{\"video_id\":\"7300000000000000000\","
                                + "\"download_url\":\"https://www.tiktok.com/@a/video/7300000000000000000\"}}");

        TikTokVideoSource video = TestSupport.client(transport)
                .accounts()
                .lookupTikTokVideo("a1", "https://www.tiktok.com/@a/video/7300000000000000000");

        assertEquals("POST", transport.last().method());
        assertEquals("7300000000000000000", video.videoId());
        assertNotNull(video.downloadUrl());
    }

    @Test
    void instagramStoriesAskForInsightsOnlyWhenRequested() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[{\"id\":\"s1\",\"media_type\":\"IMAGE\"}]}")
                .enqueue(200, "{\"data\":[{\"id\":\"s1\",\"media_type\":\"IMAGE\",\"insights\":{\"views\":40}}]}");
        FoPost client = TestSupport.client(transport);

        client.accounts().listInstagramStories("a1", false);
        assertEquals("https://api.fopost.test/v1/accounts/a1/instagram/stories", transport.last().url());

        List<InstagramStory> stories = client.accounts().listInstagramStories("a1", true);
        assertEquals(
                "https://api.fopost.test/v1/accounts/a1/instagram/stories?insights=true",
                transport.last().url());
        assertEquals(40, stories.get(0).insights().get("views"));
    }

    @Test
    void linkedInMentionsCarryTheAnnotationToPaste() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"urn":"urn:li:organization:2414183","name":"Devtestco",
                                  "annotation":"@[Devtestco](urn:li:organization:2414183)"}]}""");

        List<LinkedInMention> mentions =
                TestSupport.client(transport).accounts().searchLinkedInMentions("a1", "devtestco");

        assertEquals(
                "https://api.fopost.test/v1/accounts/a1/linkedin/mentions?q=devtestco",
                transport.last().url());
        assertEquals("@[Devtestco](urn:li:organization:2414183)", mentions.get(0).annotation());
    }
}
