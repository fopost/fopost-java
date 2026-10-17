package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.BlogArticle;
import com.fopost.sdk.model.BlogArticleFull;
import com.fopost.sdk.model.BlogArticleStatus;
import com.fopost.sdk.model.BlogGoal;
import com.fopost.sdk.model.BlogKeyword;
import com.fopost.sdk.model.BlogKeywordStatus;
import com.fopost.sdk.model.BlogSettings;
import com.fopost.sdk.model.BlogSettingsUpdate;
import com.fopost.sdk.model.BlogTargetKind;
import com.fopost.sdk.model.BlogWebsiteTest;
import com.fopost.sdk.param.BlogParams;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class BlogTest {

    private static final String SETTINGS =
            """
            {"siteUrl":"https://yourbrand.com","topic":"Scheduling","audience":"Marketers",
             "goal":"leads","focusTopics":"Scheduling tips","avoidTopics":"Competitor brands",
             "language":"en","locationCode":2840,"cadencePerWeek":2,"reviewHoldDays":3,
             "imageTier":"standard","inBodyImages":2,"minWords":900,
             "maxWords":1500,"brandVoiceId":null,"targetKind":"website","targetAccountId":null,
             "targetPlatform":null,"websiteUrl":"https://yourbrand.com/hooks/fopost",
             "hasWebsiteSecret":true,"keywordDataAvailable":true,
             "planRun":{"status":"done","error":null,"added":12,"finishedAt":"2026-10-11T09:05:00.000Z"},
             "updatedAt":"2026-10-11T09:00:00.000Z"}""";

    private static final String KEYWORD =
            """
            {"id":"kw_1","keyword":"how to schedule posts","searchVolume":1200,"difficulty":34,
             "cpc":1.25,"intent":"informational","source":"provider","status":"planned",
             "plannedFor":"2026-10-14","fitReason":"Readers who schedule posts need a tool",
             "createdAt":"2026-10-11T09:00:00.000Z"}""";

    private static final String ARTICLE =
            """
            {"id":"art_1","title":"How to schedule posts","slug":"how-to-schedule-posts",
             "keyword":"how to schedule posts","excerpt":null,"metaTitle":null,"metaDescription":null,
             "featuredImage":{"mediaId":"m_1","url":"https://cdn.example/f.png","alt":"Calendar"},
             "tags":["scheduling"],"wordCount":0,"status":"queued","origin":"user",
             "reviewUntil":null,"publishAt":null,"publishedAt":null,"publishedUrl":null,"error":null,
             "createdAt":"2026-10-11T09:00:00.000Z","updatedAt":"2026-10-11T09:00:00.000Z"}""";

    @Test
    void settingsTravelOnTheQueryForAReadAndInTheBodyForAnUpdate() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":" + SETTINGS + "}")
                .enqueue(200, "{\"data\":" + SETTINGS + ",\"websiteSecret\":\"whsec_abc\"}")
                .enqueue(200, "{\"data\":{\"websiteSecret\":\"whsec_def\"}}")
                .enqueue(502, "{\"data\":{\"ok\":false,\"status\":500,\"error\":\"HTTP 500\"}}");
        FoPost client = TestSupport.client(transport);

        BlogSettings settings = client.blog().getSettings("w1");
        assertEquals("https://api.fopost.test/v1/blog/settings?workspaceId=w1", transport.last().url());
        assertEquals(BlogTargetKind.WEBSITE, settings.targetKind());
        assertEquals(2, settings.cadencePerWeek());
        assertTrue(settings.hasWebsiteSecret());
        assertEquals(Instant.parse("2026-10-11T09:00:00Z"), settings.updatedAt());
        assertEquals(BlogGoal.LEADS, settings.goal());
        assertEquals("Scheduling tips", settings.focusTopics());
        assertEquals("Competitor brands", settings.avoidTopics());
        assertEquals("done", settings.planRun().status());
        assertEquals(12, settings.planRun().added());
        assertEquals(Instant.parse("2026-10-11T09:05:00Z"), settings.planRun().finishedAt());

        BlogSettingsUpdate updated = client.blog().updateSettings(
                "w1",
                BlogParams.Settings.create()
                        .websiteUrl("https://yourbrand.com/hooks/fopost")
                        .targetKind(BlogTargetKind.WEBSITE)
                        .brandVoiceId(null)
                        .cadencePerWeek(2)
                        .goal(BlogGoal.LEADS)
                        .focusTopics("Scheduling tips")
                        .avoidTopics(null));
        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/blog/settings", transport.last().url());
        assertEquals(
                "{\"workspaceId\":\"w1\",\"websiteUrl\":\"https://yourbrand.com/hooks/fopost\","
                        + "\"targetKind\":\"website\",\"brandVoiceId\":null,\"cadencePerWeek\":2,"
                        + "\"goal\":\"leads\",\"focusTopics\":\"Scheduling tips\",\"avoidTopics\":null}",
                transport.lastBody());
        assertEquals("whsec_abc", updated.websiteSecret());
        assertEquals("en", updated.settings().language());

        assertEquals("whsec_def", client.blog().rotateWebsiteSecret("w1"));
        assertEquals("https://api.fopost.test/v1/blog/settings/website-secret", transport.last().url());
        assertEquals("{\"workspaceId\":\"w1\"}", transport.lastBody());

        BlogWebsiteTest test = client.blog().testWebsite("w1");
        assertEquals("https://api.fopost.test/v1/blog/settings/website-test", transport.last().url());
        assertFalse(test.ok());
        assertEquals(500, test.status());
        assertEquals("HTTP 500", test.error());
    }

    @Test
    void keywordsFilterOnTheQueryAndClearAPlannedDateWithNull() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[" + KEYWORD + "]}")
                .enqueue(202, "{\"data\":{\"status\":\"queued\"}}")
                .enqueue(201, "{\"data\":" + KEYWORD + "}")
                .enqueue(200, "{\"data\":" + KEYWORD + "}")
                .enqueue(204, "");
        FoPost client = TestSupport.client(transport);

        List<BlogKeyword> keywords = client.blog().listKeywords(
                "w1", BlogParams.Filter.create().status(BlogKeywordStatus.SUGGESTED, BlogKeywordStatus.PLANNED).limit(20));
        assertEquals(
                "https://api.fopost.test/v1/blog/keywords?workspaceId=w1&status=suggested%2Cplanned&limit=20",
                transport.last().url());
        assertEquals(1200, keywords.get(0).searchVolume());
        assertEquals(LocalDate.of(2026, 10, 14), keywords.get(0).plannedFor());
        assertEquals(1.25, keywords.get(0).cpc());
        assertEquals("Readers who schedule posts need a tool", keywords.get(0).fitReason());

        client.blog().planKeywords("w1", List.of("scheduling", "analytics"));
        assertEquals("https://api.fopost.test/v1/blog/keywords/plan", transport.last().url());
        assertEquals("{\"workspaceId\":\"w1\",\"seeds\":[\"scheduling\",\"analytics\"]}", transport.lastBody());

        BlogKeyword added = client.blog().addKeyword(
                "w1", BlogParams.Keyword.of("how to schedule posts").plannedFor(LocalDate.of(2026, 10, 14)));
        assertEquals("https://api.fopost.test/v1/blog/keywords", transport.last().url());
        assertEquals(
                "{\"workspaceId\":\"w1\",\"keyword\":\"how to schedule posts\",\"plannedFor\":\"2026-10-14\"}",
                transport.lastBody());
        assertEquals("kw_1", added.id());

        client.blog().updateKeyword(
                "w1", "kw_1", BlogParams.KeywordUpdate.create().status(BlogKeywordStatus.DISMISSED).plannedFor(null));
        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/blog/keywords/kw_1?workspaceId=w1", transport.last().url());
        assertEquals("{\"status\":\"dismissed\",\"plannedFor\":null}", transport.lastBody());

        client.blog().deleteKeyword("w1", "kw_1");
        assertEquals("DELETE", transport.last().method());
        assertEquals("https://api.fopost.test/v1/blog/keywords/kw_1?workspaceId=w1", transport.last().url());
        assertNull(transport.last().body());
    }

    @Test
    void articlesAreRequestedInTheBodyAndActedOnByPath() {
        String full = ARTICLE.substring(0, ARTICLE.length() - 1)
                + ",\"instructions\":\"Keep it short\",\"outline\":[\"Why\",\"How\"],"
                + "\"brief\":{\"sources\":2},\"bodyMarkdown\":\"# Hi\",\"bodyHtml\":\"<h1>Hi</h1>\","
                + "\"images\":[{\"mediaId\":\"m_2\",\"url\":\"https://cdn.example/i.png\",\"alt\":\"Step\",\"position\":1}]}";
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[" + ARTICLE + "]}")
                .enqueue(202, "{\"data\":" + ARTICLE + "}")
                .enqueue(200, "{\"data\":" + full + "}")
                .enqueue(200, "{\"data\":" + full + "}")
                .enqueue(202, "{\"data\":" + ARTICLE + "}")
                .enqueue(202, "{\"data\":" + ARTICLE + "}")
                .enqueue(200, "{\"data\":" + ARTICLE + "}")
                .enqueue(204, "")
                .enqueue(402, "{\"error\":\"insufficient_credits\",\"message\":\"Balance\",\"upgrade_url\":\"https://fopost.com/billing\"}")
                .enqueue(409, "{\"error\":\"no_target\",\"message\":\"Pick a target first\"}");
        FoPost client = TestSupport.client(transport);

        List<BlogArticle> articles = client.blog().listArticles(
                "w1", BlogParams.Filter.create().status(BlogArticleStatus.DRAFT));
        assertEquals("https://api.fopost.test/v1/blog/articles?workspaceId=w1&status=draft", transport.last().url());
        assertEquals("Calendar", articles.get(0).featuredImage().alt());
        assertEquals(List.of("scheduling"), articles.get(0).tags());

        BlogArticle requested = client.blog().requestArticle(
                "w1", BlogParams.Article.create().keyword("how to schedule posts").instructions("Keep it short"));
        assertEquals("https://api.fopost.test/v1/blog/articles", transport.last().url());
        assertEquals(
                "{\"workspaceId\":\"w1\",\"keyword\":\"how to schedule posts\",\"instructions\":\"Keep it short\"}",
                transport.lastBody());
        assertEquals(BlogArticleStatus.QUEUED, requested.status());

        BlogArticleFull fetched = client.blog().getArticle("w1", "art_1");
        assertEquals("https://api.fopost.test/v1/blog/articles/art_1?workspaceId=w1", transport.last().url());
        assertEquals("# Hi", fetched.bodyMarkdown());
        assertEquals(List.of("Why", "How"), fetched.outline());
        assertEquals(2, fetched.brief().path("sources").asInt());
        assertEquals(1, fetched.images().get(0).position());

        client.blog().updateArticle(
                "w1", "art_1", BlogParams.ArticleUpdate.create().title("New title").tags(List.of("a", "b")).publishAt(null));
        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/blog/articles/art_1?workspaceId=w1", transport.last().url());
        assertEquals("{\"title\":\"New title\",\"tags\":[\"a\",\"b\"],\"publishAt\":null}", transport.lastBody());

        client.blog().regenerateArticle("w1", "art_1");
        assertEquals("https://api.fopost.test/v1/blog/articles/art_1/regenerate?workspaceId=w1", transport.last().url());
        client.blog().publishArticle("w1", "art_1");
        assertEquals("https://api.fopost.test/v1/blog/articles/art_1/publish?workspaceId=w1", transport.last().url());
        assertEquals("POST", transport.last().method());
        assertNull(transport.last().body());
        client.blog().rejectArticle("w1", "art_1");
        assertEquals("https://api.fopost.test/v1/blog/articles/art_1/reject?workspaceId=w1", transport.last().url());
        client.blog().deleteArticle("w1", "art_1");
        assertEquals("DELETE", transport.last().method());
        assertEquals("https://api.fopost.test/v1/blog/articles/art_1?workspaceId=w1", transport.last().url());

        PaymentRequiredException noCredits = assertThrows(
                PaymentRequiredException.class,
                () -> client.blog().requestArticle("w1", BlogParams.Article.create().title("Again")));
        assertEquals("insufficient_credits", noCredits.code());
        assertEquals("https://fopost.com/billing", noCredits.upgradeUrl());

        FoPostException noTarget = assertThrows(FoPostException.class, () -> client.blog().publishArticle("w1", "art_1"));
        assertEquals(409, noTarget.status());
        assertEquals("no_target", noTarget.code());
    }

    @Test
    void suggestSeedsSendsTheCurrentSeedsAndPromptAndReturnsTheTerms() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"seeds\":[\"schedule posts\",\"best time to post\"]}}")
                .enqueue(200, "{\"data\":{\"seeds\":[]}}")
                .enqueue(402, "{\"error\":\"insufficient_credits\",\"message\":\"Balance\"}");
        FoPost client = TestSupport.client(transport);

        List<String> seeds = client.blog().suggestSeeds("w1", List.of("scheduling"), "Small teams");
        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/blog/keywords/suggest", transport.last().url());
        assertEquals(
                "{\"workspaceId\":\"w1\",\"seeds\":[\"scheduling\"],\"prompt\":\"Small teams\"}",
                transport.lastBody());
        assertEquals(List.of("schedule posts", "best time to post"), seeds);

        assertEquals(List.of(), client.blog().suggestSeeds("w1"));
        assertEquals("{\"workspaceId\":\"w1\"}", transport.lastBody());

        assertThrows(PaymentRequiredException.class, () -> client.blog().suggestSeeds("w1"));
    }

    @Test
    void submitArticleMovesItToReviewByPath() {
        String inReview = ARTICLE.replace("\"status\":\"queued\"", "\"status\":\"in_review\"");
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":" + inReview + "}")
                .enqueue(409, "{\"error\":\"invalid_state\",\"message\":\"Only a draft can be submitted\"}");
        FoPost client = TestSupport.client(transport);

        BlogArticle submitted = client.blog().submitArticle("w1", "art_1");
        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/blog/articles/art_1/submit?workspaceId=w1", transport.last().url());
        assertNull(transport.last().body());
        assertEquals(BlogArticleStatus.IN_REVIEW, submitted.status());
        assertTrue(BlogArticleStatus.ALL.contains(submitted.status()));

        FoPostException conflict = assertThrows(FoPostException.class, () -> client.blog().submitArticle("w1", "art_1"));
        assertEquals(409, conflict.status());
        assertEquals("invalid_state", conflict.code());
    }
}
