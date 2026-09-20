package com.fopost.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.internal.JdkTransport;
import com.fopost.sdk.internal.Sleeper;
import com.fopost.sdk.internal.Transport;
import com.fopost.sdk.internal.Version;
import com.fopost.sdk.resource.AccountGroupsResource;
import com.fopost.sdk.resource.AccountsResource;
import com.fopost.sdk.resource.AdsResource;
import com.fopost.sdk.resource.AiResource;
import com.fopost.sdk.resource.AnalyticsResource;
import com.fopost.sdk.resource.AutomationsResource;
import com.fopost.sdk.resource.BroadcastsResource;
import com.fopost.sdk.resource.ContactsResource;
import com.fopost.sdk.resource.InboxResource;
import com.fopost.sdk.resource.KnowledgeResource;
import com.fopost.sdk.resource.LabelsResource;
import com.fopost.sdk.resource.MediaResource;
import com.fopost.sdk.resource.PostsResource;
import com.fopost.sdk.resource.SequencesResource;
import com.fopost.sdk.resource.ValidateResource;
import com.fopost.sdk.resource.WebhooksResource;
import com.fopost.sdk.resource.WorkspacesResource;
import java.time.Duration;
import java.util.Map;

/**
 * Client for the FoPost API.
 *
 * <pre>{@code
 * FoPost client = FoPost.create("fp_...");           // or set FOPOST_API_KEY
 *
 * Workspace workspace = client.workspaces().list().get(0);
 * List<Account> accounts = client.accounts().list(workspace.id());
 *
 * Post post = client.posts().create(
 *         CreatePostParams.of(workspace.id())
 *                 .content("Hello from Java")
 *                 .accounts(accounts.get(0).id()));
 *
 * client.posts().publish(post.id());
 * }</pre>
 *
 * <p>The key falls back to the {@code FOPOST_API_KEY} environment variable. A response of 429 is
 * retried automatically, waiting for the interval the API asks for in {@code Retry-After}.
 *
 * <p>Instances are immutable and safe to share across threads.
 */
public final class FoPost {

    /** The version of this SDK. */
    public static final String VERSION = Version.VALUE;

    /** Where the API lives, unless a builder says otherwise. */
    public static final String DEFAULT_BASE_URL = ApiClient.DEFAULT_BASE_URL;

    /** The environment variable read when no key is passed. */
    public static final String API_KEY_ENV = "FOPOST_API_KEY";

    private final ApiClient http;
    private final PostsResource posts;
    private final AccountsResource accounts;
    private final AccountGroupsResource accountGroups;
    private final WorkspacesResource workspaces;
    private final KnowledgeResource knowledge;
    private final LabelsResource labels;
    private final WebhooksResource webhooks;
    private final AnalyticsResource analytics;
    private final AutomationsResource automations;
    private final MediaResource media;
    private final AiResource ai;
    private final ContactsResource contacts;
    private final BroadcastsResource broadcasts;
    private final SequencesResource sequences;
    private final InboxResource inbox;
    private final AdsResource ads;
    private final ValidateResource validate;

    private FoPost(ApiClient http) {
        this.http = http;
        this.posts = new PostsResource(http);
        this.accounts = new AccountsResource(http);
        this.accountGroups = new AccountGroupsResource(http);
        this.workspaces = new WorkspacesResource(http);
        this.knowledge = new KnowledgeResource(http);
        this.labels = new LabelsResource(http);
        this.webhooks = new WebhooksResource(http);
        this.analytics = new AnalyticsResource(http);
        this.automations = new AutomationsResource(http);
        this.media = new MediaResource(http);
        this.ai = new AiResource(http);
        this.inbox = new InboxResource(http);
        this.contacts = new ContactsResource(http);
        this.broadcasts = new BroadcastsResource(http);
        this.sequences = new SequencesResource(http);
        this.ads = new AdsResource(http);
        this.validate = new ValidateResource(http);
    }

    /** A client reading its key from {@code FOPOST_API_KEY}. */
    public static FoPost create() {
        return builder().build();
    }

    public static FoPost create(String apiKey) {
        return builder().apiKey(apiKey).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public PostsResource posts() {
        return posts;
    }

    public AccountsResource accounts() {
        return accounts;
    }

    public AccountGroupsResource accountGroups() {
        return accountGroups;
    }

    public WorkspacesResource workspaces() {
        return workspaces;
    }

    /** The workspace knowledge base, which grounds drafted replies. */
    public KnowledgeResource knowledge() {
        return knowledge;
    }

    public LabelsResource labels() {
        return labels;
    }

    public WebhooksResource webhooks() {
        return webhooks;
    }

    public AnalyticsResource analytics() {
        return analytics;
    }

    public AutomationsResource automations() {
        return automations;
    }

    public MediaResource media() {
        return media;
    }

    public AiResource ai() {
        return ai;
    }

    public InboxResource inbox() {
        return inbox;
    }

    /** The people behind the inbox, and the fields kept about them. */
    public ContactsResource contacts() {
        return contacts;
    }

    /**
     * One message into every conversation the workspace already has with a segment of its
     * contacts.
     */
    public BroadcastsResource broadcasts() {
        return broadcasts;
    }

    /** A series of messages on a delay, walked per enrolled contact. */
    public SequencesResource sequences() {
        return sequences;
    }

    public AdsResource ads() {
        return ads;
    }

    public ValidateResource validate() {
        return validate;
    }

    public String baseUrl() {
        return http.baseUrl();
    }

    /**
     * Call an endpoint this SDK does not wrap yet, authenticated like any other call.
     *
     * <pre>{@code
     * JsonNode body = client.request("GET", "/v1/analytics/overview", null, Map.of("days", 30));
     * }</pre>
     */
    public JsonNode request(String method, String path, Object body, Map<String, Object> query) {
        return http.request(method, path, body, query);
    }

    /** Builds a {@link FoPost}. Everything but the key has a working default. */
    public static final class Builder {

        private String apiKey;
        private String baseUrl = ApiClient.DEFAULT_BASE_URL;
        private Duration timeout = ApiClient.DEFAULT_TIMEOUT;
        private int maxRetries = ApiClient.DEFAULT_MAX_RETRIES;
        private Transport transport;
        private Sleeper sleeper;

        private Builder() {}

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /** Point at another deployment, e.g. {@code http://localhost:8080} in development. */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /** Per-request timeout. 30 seconds by default. */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /** Total attempts on a 429, so the default of 3 means two retries. */
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        /** Bring your own HTTP stack, or a fake in tests. */
        public Builder transport(Transport transport) {
            this.transport = transport;
            return this;
        }

        /** Test seam: how the retry loop waits. */
        Builder sleeper(Sleeper sleeper) {
            this.sleeper = sleeper;
            return this;
        }

        public FoPost build() {
            String key = apiKey != null && !apiKey.isEmpty() ? apiKey : System.getenv(API_KEY_ENV);
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException(
                        "fopost: an API key is required — pass apiKey(...) or set " + API_KEY_ENV);
            }
            Transport chosen = transport != null ? transport : new JdkTransport(timeout);
            return new FoPost(new ApiClient(key, baseUrl, maxRetries, chosen, sleeper));
        }
    }
}
