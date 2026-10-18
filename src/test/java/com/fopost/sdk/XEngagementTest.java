package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.InboxItem;
import com.fopost.sdk.model.XFollowResult;
import com.fopost.sdk.model.XPost;
import com.fopost.sdk.model.XSearchPage;
import com.fopost.sdk.param.XSearchParams;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class XEngagementTest {

    @Test
    void repostAndBookmarkPostWithoutABodyAndReadTheNewFlags() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"id":"i1","platform":"twitter","liked":false,"reposted":true,
                                 "bookmarked":false,"can_like":true,"can_repost":true,"can_bookmark":true}}""")
                .enqueue(200, "{\"data\":{\"id\":\"i1\",\"reposted\":false}}")
                .enqueue(200, "{\"data\":{\"id\":\"i1\",\"bookmarked\":true,\"canBookmark\":true}}")
                .enqueue(200, "{\"data\":{\"id\":\"i1\",\"bookmarked\":false}}");
        FoPost client = TestSupport.client(transport);

        InboxItem reposted = client.inbox().repost("i1");
        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/inbox/i1/repost", transport.last().url());
        assertEquals("", transport.lastBody());
        assertTrue(reposted.reposted());
        assertFalse(reposted.bookmarked());
        assertTrue(reposted.canRepost());
        assertTrue(reposted.canBookmark());

        assertFalse(client.inbox().unrepost("i1").reposted());
        assertEquals("https://api.fopost.test/v1/inbox/i1/unrepost", transport.last().url());

        InboxItem bookmarked = client.inbox().bookmark("i1");
        assertEquals("https://api.fopost.test/v1/inbox/i1/bookmark", transport.last().url());
        assertTrue(bookmarked.bookmarked());
        assertTrue(bookmarked.canBookmark());

        assertFalse(client.inbox().unbookmark("i1").bookmarked());
        assertEquals("https://api.fopost.test/v1/inbox/i1/unbookmark", transport.last().url());
    }

    @Test
    void followAndUnfollowSendTheUserIdAndReadTheState() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"account_id\":\"a1\",\"user_id\":\"2244994945\",\"following\":true}}")
                .enqueue(200, "{\"data\":{\"account_id\":\"a1\",\"user_id\":\"2244994945\",\"following\":false}}");
        FoPost client = TestSupport.client(transport);

        XFollowResult followed = client.accounts().followXUser("a1", "2244994945");
        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/x/follow", transport.last().url());
        assertEquals("{\"user_id\":\"2244994945\"}", transport.lastBody());
        assertEquals("a1", followed.accountId());
        assertEquals("2244994945", followed.userId());
        assertTrue(followed.following());

        XFollowResult unfollowed = client.accounts().unfollowXUser("a1", "2244994945");
        assertEquals("https://api.fopost.test/v1/accounts/a1/x/unfollow", transport.last().url());
        assertEquals("{\"user_id\":\"2244994945\"}", transport.lastBody());
        assertFalse(unfollowed.following());
    }

    @Test
    void postLookupNamesTheAccountItReadsAs() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"id":"1790000000000000000","text":"Shipping today",
                                 "permalink":"https://x.com/yourbrand/status/1790000000000000000",
                                 "author_name":"Your Brand","author_handle":"yourbrand",
                                 "author_avatar_url":null,"author_followers":1200,
                                 "conversation_id":"1790000000000000000","parent_external_id":null,
                                 "likes":10,"replies":2,"reposts":3,"quotes":1,
                                 "platform_created_at":"2026-10-18T09:00:00.000Z"}}""");
        FoPost client = TestSupport.client(transport);

        XPost post = client.accounts().getXPost("1790000000000000000", "a1");

        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/x/posts/1790000000000000000?account_id=a1", transport.last().url());
        assertEquals("Shipping today", post.text());
        assertEquals("yourbrand", post.authorHandle());
        assertEquals(1200L, post.authorFollowers());
        assertEquals(3L, post.reposts());
        assertNull(post.parentExternalId());
        assertEquals(Instant.parse("2026-10-18T09:00:00Z"), post.platformCreatedAt());
    }

    @Test
    void searchEncodesTheQueryAndCursorAndReadsTheNextToken() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":[{"id":"1","text":"first"},{"id":"2","text":"second"}],
                         "meta":{"next_token":"b26v89c19zqg8o3fpdy"}}""")
                .enqueue(200, "{\"data\":[],\"meta\":{\"next_token\":null}}");
        FoPost client = TestSupport.client(transport);

        XSearchPage page = client.accounts().searchXPosts(
                XSearchParams.create("a1", "from:yourbrand -is:retweet").maxResults(50));

        assertEquals("GET", transport.last().method());
        assertEquals(
                "https://api.fopost.test/v1/x/search?account_id=a1&q=from%3Ayourbrand+-is%3Aretweet&max_results=50",
                transport.last().url());
        assertEquals(2, page.size());
        assertEquals("second", page.data().get(1).text());
        assertEquals("b26v89c19zqg8o3fpdy", page.meta().nextToken());

        XSearchPage last = client.accounts().searchXPosts(
                XSearchParams.create("a1", "from:yourbrand").nextToken(page.meta().nextToken()));
        assertEquals(
                "https://api.fopost.test/v1/x/search?account_id=a1&q=from%3Ayourbrand&next_token=b26v89c19zqg8o3fpdy",
                transport.last().url());
        assertTrue(last.isEmpty());
        assertNull(last.meta().nextToken());
    }
}
