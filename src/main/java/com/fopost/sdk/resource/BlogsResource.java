package com.fopost.sdk.resource;

import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.model.RemoteArticle;
import com.fopost.sdk.model.RemoteBlog;
import com.fopost.sdk.model.RemoteProduct;
import com.fopost.sdk.param.ArticleListParams;
import com.fopost.sdk.param.ArticleParams;
import com.fopost.sdk.param.ProductListParams;
import com.fopost.sdk.param.ProductParams;
import java.util.List;
import java.util.Map;

/**
 * Articles and products that already live on a connected site, addressed by the
 * platform's own ids rather than FoPost ids.
 *
 * <p>Reads need the {@code posts} scope; anything that changes the site needs
 * {@code posts} and {@code publish}. An account on a platform that cannot manage
 * articles answers 400 {@code unsupported_platform}.
 *
 * <p>An update changes the live article in place and never creates a second
 * post, so a link already shared keeps working.
 */
public final class BlogsResource {

    private final ApiClient http;

    public BlogsResource(ApiClient http) {
        this.http = http;
    }

    private static String base(String accountId) {
        return "/v1/accounts/" + accountId + "/blogs";
    }

    /** The blogs the account can write to. WordPress reports one, under {@code default}. */
    public List<RemoteBlog> listBlogs(String accountId) {
        return http.convertList(
                ApiClient.unwrap(http.get(base(accountId), null)), RemoteBlog.class);
    }

    public List<RemoteArticle> listArticles(String accountId, String blogId) {
        return listArticles(accountId, blogId, null);
    }

    public List<RemoteArticle> listArticles(
            String accountId, String blogId, ArticleListParams params) {
        Map<String, Object> query = params == null ? null : params.toQuery();
        return http.convertList(
                ApiClient.unwrap(http.get(base(accountId) + "/" + blogId + "/articles", query)),
                RemoteArticle.class);
    }

    public RemoteArticle getArticle(String accountId, String blogId, String articleId) {
        return http.convert(
                ApiClient.unwrap(
                        http.get(base(accountId) + "/" + blogId + "/articles/" + articleId, null)),
                RemoteArticle.class);
    }

    /** Writes a new article. Needs the {@code publish} scope. */
    public RemoteArticle createArticle(String accountId, String blogId, ArticleParams params) {
        return http.convert(
                ApiClient.unwrap(
                        http.post(base(accountId) + "/" + blogId + "/articles", params.toBody())),
                RemoteArticle.class);
    }

    /** Changes the live article in place; never creates a duplicate. */
    public RemoteArticle updateArticle(
            String accountId, String blogId, String articleId, ArticleParams params) {
        return http.convert(
                ApiClient.unwrap(
                        http.request(
                                "PATCH",
                                base(accountId) + "/" + blogId + "/articles/" + articleId,
                                params.toBody(),
                                null)),
                RemoteArticle.class);
    }

    /** Removes the article from the site. This cannot be undone. */
    public void deleteArticle(String accountId, String blogId, String articleId) {
        http.delete(base(accountId) + "/" + blogId + "/articles/" + articleId);
    }

    public List<RemoteProduct> listProducts(String accountId) {
        return listProducts(accountId, null);
    }

    public List<RemoteProduct> listProducts(String accountId, ProductListParams params) {
        Map<String, Object> query = params == null ? null : params.toQuery();
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/products", query)),
                RemoteProduct.class);
    }

    /** Changes the product on the store. Only what is set travels. */
    public RemoteProduct updateProduct(String accountId, String productId, ProductParams params) {
        return http.convert(
                ApiClient.unwrap(
                        http.request(
                                "PATCH",
                                "/v1/accounts/" + accountId + "/products/" + productId,
                                params.toBody(),
                                null)),
                RemoteProduct.class);
    }
}
