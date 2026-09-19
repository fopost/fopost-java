package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fopost.sdk.param.ArticleListParams;
import com.fopost.sdk.param.ArticleParams;
import com.fopost.sdk.param.ProductParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class BlogsTest {

    private static final String ARTICLE =
            "{\"id\":\"99\",\"blog_id\":\"11\",\"title\":\"Spring drop\","
                    + "\"body_html\":\"<p>Hello</p>\",\"excerpt\":\"A short summary\","
                    + "\"status\":\"published\",\"author_name\":\"Store Owner\",\"tags\":[\"news\"],"
                    + "\"image_url\":null,\"url\":\"https://demo.myshopify.com/blogs/article/spring-drop\","
                    + "\"published_at\":\"2026-09-01T10:00:00.000Z\",\"updated_at\":null}";

    @Test
    void listBlogsReadsEveryBlogOnTheSite() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[{\"id\":\"11\",\"title\":\"News\",\"handle\":\"news\",\"url\":null}]}");

        var blogs = TestSupport.client(transport).blogs().listBlogs("a1");

        assertEquals("https://api.fopost.test/v1/accounts/a1/blogs", transport.last().url());
        assertEquals(1, blogs.size());
        assertEquals("11", blogs.get(0).id());
        assertEquals("News", blogs.get(0).title());
    }

    @Test
    void listArticlesSendsTheFilters() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":[" + ARTICLE + "]}");

        var articles = TestSupport.client(transport)
                .blogs()
                .listArticles("a1", "11", ArticleListParams.create().limit(5).status("draft").q("spring"));

        assertEquals(
                "https://api.fopost.test/v1/accounts/a1/blogs/11/articles?limit=5&status=draft&q=spring",
                transport.last().url());
        assertEquals("99", articles.get(0).id());
        assertEquals(List.of("news"), articles.get(0).tags());
        assertEquals("Store Owner", articles.get(0).authorName());
    }

    /**
     * The article id is in the path and only what the caller set is sent, which
     * is what stops an edit from creating a second post on the site.
     */
    @Test
    void updateArticleChangesItInPlace() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":" + ARTICLE + "}");

        TestSupport.client(transport)
                .blogs()
                .updateArticle("a1", "11", "99", ArticleParams.create().title("Spring drop, restocked"));

        assertEquals("PATCH", transport.last().method());
        assertEquals(
                "https://api.fopost.test/v1/accounts/a1/blogs/11/articles/99", transport.last().url());
        assertEquals("{\"title\":\"Spring drop, restocked\"}", transport.last().bodyAsString());
    }

    @Test
    void createArticleSendsOnlyWhatItWasGiven() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":" + ARTICLE + "}");

        TestSupport.client(transport)
                .blogs()
                .createArticle(
                        "a1", "11", ArticleParams.create().title("Spring drop").body("Hello").status("draft"));

        assertEquals("POST", transport.last().method());
        assertEquals(
                "{\"title\":\"Spring drop\",\"body\":\"Hello\",\"status\":\"draft\"}",
                transport.last().bodyAsString());
    }

    @Test
    void deleteArticleHitsTheArticleRoute() {
        FakeTransport transport = new FakeTransport().enqueue(204, "");

        TestSupport.client(transport).blogs().deleteArticle("a1", "11", "99");

        assertEquals("DELETE", transport.last().method());
        assertEquals(
                "https://api.fopost.test/v1/accounts/a1/blogs/11/articles/99", transport.last().url());
    }

    @Test
    void updateProductSendsOnlyWhatChanged() {
        FakeTransport transport = new FakeTransport()
                .enqueue(
                        200,
                        "{\"data\":{\"id\":\"7\",\"title\":\"Mug XL\",\"handle\":\"mug\",\"status\":\"draft\","
                                + "\"description\":null,\"vendor\":null,\"product_type\":\"Drinkware\","
                                + "\"tags\":[],\"image_url\":null,\"url\":null,\"price\":\"12.00\","
                                + "\"currency\":\"USD\",\"updated_at\":null}}");

        var product = TestSupport.client(transport)
                .blogs()
                .updateProduct("a1", "7", ProductParams.create().title("Mug XL").productType("Drinkware"));

        assertEquals("PATCH", transport.last().method());
        assertEquals(
                "{\"title\":\"Mug XL\",\"product_type\":\"Drinkware\"}", transport.last().bodyAsString());
        assertEquals("12.00", product.price());
    }
}
