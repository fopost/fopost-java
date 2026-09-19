package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.InboxItem;
import com.fopost.sdk.model.RedditDefaultSubreddit;
import com.fopost.sdk.model.RedditFlairs;
import com.fopost.sdk.model.RedditSubreddit;
import com.fopost.sdk.model.RedditSubredditRules;
import com.fopost.sdk.model.SubredditCheck;
import java.util.List;
import org.junit.jupiter.api.Test;

class RedditTest {

    @Test
    void subredditsRulesAndFlairsReadTheCamelCaseWire() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"name":"webdev","title":"Web Development","subscribers":2000000,
                                  "over18":false,"canPost":true,"flairEnabled":true,
                                  "iconUrl":null,"isDefault":true}]}""")
                .enqueue(200, """
                        {"data":{"subreddit":"webdev","rules":[{"name":"No self promotion",
                                  "description":"Keep it useful","appliesTo":"link"}]}}""")
                .enqueue(200, """
                        {"data":{"subreddit":"webdev","flairs":[{"id":"flair-1",
                                  "text":"Showoff Saturday","editable":false}]}}""");
        FoPost client = TestSupport.client(transport);

        List<RedditSubreddit> subreddits = client.accounts().redditSubreddits("a1");
        assertEquals("https://api.fopost.test/v1/accounts/a1/reddit/subreddits", transport.last().url());
        assertEquals("webdev", subreddits.get(0).name());
        assertTrue(subreddits.get(0).isDefault());
        assertTrue(subreddits.get(0).flairEnabled());

        RedditSubredditRules rules = client.accounts().redditSubredditRules("a1", "webdev");
        assertEquals(
                "https://api.fopost.test/v1/accounts/a1/reddit/subreddits/webdev/rules",
                transport.last().url());
        assertEquals("link", rules.rules().get(0).appliesTo());

        RedditFlairs flairs = client.accounts().redditFlairs("a1", "webdev");
        assertEquals(
                "https://api.fopost.test/v1/accounts/a1/reddit/flairs?subreddit=webdev",
                transport.last().url());
        assertEquals("flair-1", flairs.flairs().get(0).id());
    }

    @Test
    void theDefaultSubredditSendsNullToFallBackToTheProfilePage() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":{\"subreddit\":null}}");
        FoPost client = TestSupport.client(transport);

        RedditDefaultSubreddit result = client.accounts().setRedditDefaultSubreddit("a1", null);

        assertEquals("PUT", transport.last().method());
        assertEquals("{}", transport.lastBody());
        assertNull(result.subreddit());
    }

    @Test
    void validatingASubredditNamesTheAccountItReadsAs() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"subreddit":"webdev","exists":true,"can_post":true,
                                 "over_18":false,"flair_enabled":true,"ok":true}}""");
        FoPost client = TestSupport.client(transport);

        SubredditCheck check = client.validate().subreddit("a1", "webdev");

        assertEquals(
                "https://api.fopost.test/v1/validate/subreddit?account_id=a1&name=webdev",
                transport.last().url());
        assertTrue(check.ok());
        assertTrue(check.canPost());
        assertEquals(Boolean.FALSE, check.over18());
    }

    @Test
    void votingSendsTheDirection() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"id":"i1","platform":"reddit","type":"comment","state":"unread",
                                 "vote":"down","canVote":true}}""");
        FoPost client = TestSupport.client(transport);

        InboxItem item = client.inbox().vote("i1", "down");

        assertEquals("https://api.fopost.test/v1/inbox/i1/vote", transport.last().url());
        assertEquals("{\"direction\":\"down\"}", transport.lastBody());
        assertEquals("down", item.vote());
        assertTrue(item.canVote());
    }
}
