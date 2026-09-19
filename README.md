# fopost-java

[![CI](https://github.com/fopost/fopost-java/actions/workflows/ci.yml/badge.svg)](https://github.com/fopost/fopost-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Official Java SDK for the [FoPost](https://fopost.com) API. Schedule and publish to +30 social platforms from your code.

```xml
<dependency>
  <groupId>com.fopost</groupId>
  <artifactId>fopost-java</artifactId>
  <version>0.2.0</version>
</dependency>
```

```kotlin
implementation("com.fopost:fopost-java:0.2.0")
```

Requires Java 17 or newer. HTTP goes through the JDK's own client; the only dependency is Jackson.

> **0.x release.** The public API is still settling and minor versions may contain breaking
> changes. Pin an exact version if that matters to you.

## Quick start

```java
import com.fopost.sdk.FoPost;
import com.fopost.sdk.model.*;
import com.fopost.sdk.param.*;

FoPost client = FoPost.create("fp_...");   // or set FOPOST_API_KEY

Workspace workspace = client.workspaces().list().get(0);
List<Account> accounts = client.accounts().list(workspace.id());

Post post = client.posts().create(
        CreatePostParams.of(workspace.id())
                .content("Hello from Java")
                .accounts(accounts.stream().map(Account::id).toList()));

client.posts().publish(post.id());
```

A post is one or more content blocks. One block is a plain update; several make a thread:

```java
client.posts().create(CreatePostParams.of(workspace.id())
        .accounts(accountId)
        .content("First post in the thread")
        .block(ContentBlockInput.text("Second one, with an image")
                .media(MediaItem.of("image", "chart.png", "https://.../chart.png"))));
```

## Scheduling

`status` is `draft` or `scheduled`, and a scheduled post needs a time. To send something out now,
create it and call `publish`.

```java
client.posts().create(CreatePostParams.of(workspace.id())
        .accounts(accountId)
        .content("Scheduled with the SDK")
        .schedule(Instant.parse("2026-09-01T10:00:00Z")));
```

Before publishing, `preflight` reports the per-account blockers and advisory signals without
sending anything, and `publish` with `dryRun` rehearses the whole thing:

```java
PreflightResult check = client.posts().preflight(post.id());
if (!check.isReady()) {
    check.accounts().forEach(a -> System.out.println(a.platform() + ": " + a.issues()));
}
```

## Pagination

`list` returns one page and iterates over its items. `autoPaginate` walks every page for you,
fetching each one as you read it:

```java
Page<Post> page = client.posts().list(
        PostListParams.create().workspaceId(workspace.id()).status(PostStatus.PUBLISHED).perPage(50));
System.out.println(page.meta().total() + " published posts");

for (Post post : client.posts().autoPaginate(PostListParams.create().workspaceId(workspace.id()))) {
    System.out.println(post.id());
}

long failed = client.posts().stream(PostListParams.create().workspaceId(workspace.id()))
        .filter(p -> PostStatus.FAILED.equals(p.status()))
        .count();
```

## Resources

| Namespace       | Methods                                                                                                                                                                                     |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `posts()`       | `list`, `autoPaginate`, `stream`, `get`, `create`, `update`, `delete`, `duplicate`, `publish`, `retry`, `cancel`, `preflight`, `deliveries`, `publishRuns`, `analytics`, `bulkShift`, `bulkLabel`, `bulkDelete`, `validateImport`, `commitImport`, `rollbackImport` |
| `workspaces()`  | `list`, `get`, `create`, `update`, `delete`, `analytics`                                                                                                                                     |
| `accounts()`    | `list`, `get`, `create`, `delete`, `healthSummary`, `health`, `togglePrimary`, `validate`, `refreshToken`, `analytics`, `communities()`                                                       |
| `labels()`      | `list`, `get`, `create`, `update`, `delete`                                                                                                                                                  |
| `webhooks()`    | `list`, `create`, `update`, `delete`, `test`                                                                                                                                                 |
| `analytics()`   | `overview`, `timeSeries`, `topPosts`, `labels`, `postsTable`, `postingStreak`, `demographics`, `collect`                                                                                     |
| `automations()` | `list`, `get`, `create`, `update`, `delete`, `toggle`, `runs`, `run`, `trigger`, `stats`                                                                                                     |
| `media()`       | `list`, `upload`, `delete`                                                                                                                                                                   |
| `ai()`          | `credits`, `generateCaption`, `rewrite`, `repurposeUrl`                                                                                                                                      |
| `inbox()`       | `list`, `threads`, `conversations`, `unreadCount`, `accounts`, `platforms`, `markThreadRead`, `markConversationRead`, `refresh`, `update`, `reply`, `hide`, `unhide`, `delete`, `listApprovals`, `approveReply`, `rejectReply` |
| `ads()`         | `list`, `external`, `boostable`, `connections`, `sources`, `authorizeMeta`, `deleteConnection`, `boost`, `create`, `refresh`, `setStatus`, `delete`, `audiences`, `createAudience`, `searchTargeting`, `leadForms`, `createLeadForm`, `leads` |

`accounts().communities()` covers the X communities an account can post into: `list`, `sync`,
`search`, `add`, `remove`.

For an endpoint the SDK does not wrap yet, `request` sends an authenticated call and hands back
the decoded body:

```java
JsonNode body = client.request("GET", "/v1/analytics/overview", null, Map.of("days", 30));
```

## Media

Upload once, then attach the returned file to a content block:

```java
UploadedMedia file = client.media().upload(workspace.id(), Path.of("chart.png")).get(0);

client.posts().create(CreatePostParams.of(workspace.id())
        .accounts(accountId)
        .block(ContentBlockInput.text("Numbers are in").media(file.toMediaItem())));
```

## Webhooks

The signing secret is returned by the create call and never shown again — store it then.

```java
Webhook hook = client.webhooks().create(
        workspace.id(),
        "https://example.com/hooks/fopost",
        List.of(WebhookEvents.POST_PUBLISHED, WebhookEvents.DELIVERY_FAILED));

System.out.println(hook.secret());
client.webhooks().test(hook.id());
```

## AI features

```java
AiCreditBalance balance = client.ai().credits();
System.out.println(balance.creditsRemaining() + " of " + balance.creditsTotal() + " credits left");

CaptionResult caption = client.ai().generateCaption(CaptionParams.create()
        .currentCaption("shipping a new feature")
        .platforms(Platforms.TWITTER, Platforms.LINKEDIN));
```

> **API keys reach `credits` and `generateCaption`.** `rewrite` and `repurposeUrl` currently
> require a signed-in dashboard session and answer `401` to an API key. They are here so the
> surface is complete once the server opens them up.

## Inbox

Comments, mentions and direct messages across the connected accounts, with the state of each
(`unread`, `read`, `resolved`, `snoozed`). Lists are pages with `page`, `perPage` and `total`.

```java
InboxPage<InboxItem> unread = client.inbox().list(
        InboxListParams.create().workspaceId(workspace.id()).state("unread"));

for (InboxItem item : unread) {
    if (Boolean.TRUE.equals(item.canReply())) {
        client.inbox().reply(item.id(), "Thanks for asking, sent you a DM.");
    }
}

client.inbox().markThreadRead(workspace.id(), accountId, postExternalId);
client.inbox().update(itemId, "snoozed", Instant.parse("2026-09-02T09:00:00Z"));
```

`threads` groups comments per platform post (`kind("mentions")` for posts the account was tagged
in) and `conversations` per DM thread. `listApprovals` returns replies an automation or the agent
drafted that a person still has to send; `approveReply` sends the draft (or your edited text) and
`rejectReply` discards it.

## Ads

Boosts, standalone ads, audiences and lead forms on the connected ad accounts.

```java
BoostablePost candidate = client.ads().boostable(workspace.id()).get(0);

Ad boost = client.ads().boost(BoostPostParams.of(
        workspace.id(), connectionId, "act_123",
        candidate.id(), candidate.deliveries().get(0).accountId(),
        "Launch week", "engagement",
        AdBudgetParams.daily(2000),
        AdTargetingParams.create(List.of("US"), 21, 45, "all")));

client.ads().setStatus(boost.id(), workspace.id(), "active");
```

A boost or ad starts paused unless `paused(false)` is set, so nothing is spent until it is
resumed. `boost`, `create`, `setStatus` and `delete` need the `publish` scope as well as `ads`.

## Configuration

```java
FoPost client = FoPost.builder()
        .apiKey("fp_...")                          // or FOPOST_API_KEY
        .baseUrl("https://api.fopost.com")         // override for a dev server
        .timeout(Duration.ofSeconds(30))
        .maxRetries(3)                             // total attempts on a 429
        .transport(myTransport)                    // bring your own HTTP stack
        .build();
```

| Env var          | Used for                                    |
| ---------------- | ------------------------------------------- |
| `FOPOST_API_KEY` | API key, when none is passed to the builder |

Clients are immutable and safe to share across threads. A `429` is retried automatically, waiting
for the interval the API asks for in `Retry-After` (delta-seconds or an HTTP date, capped at 60s).
`maxRetries` counts total attempts, so the default of 3 means two retries.

## Error handling

Every non-2xx response throws `FoPostException` or one of its subclasses, carrying the API's
status, code, and message. They are unchecked, so nothing forces a `try` you did not want.

```java
try {
    client.posts().publish(postId);
} catch (PaymentRequiredException e) {
    System.out.println("Out of credits — upgrade at " + e.upgradeUrl());
} catch (RateLimitException e) {
    System.out.println("Rate limited, retry in " + e.retryAfter());
} catch (FoPostException e) {
    System.out.println("API " + e.status() + " (" + e.code() + "): " + e.getMessage());
}
```

| Status   | Exception                    |
| -------- | ---------------------------- |
| 400, 422 | `ValidationException`        |
| 401      | `AuthenticationException`    |
| 402      | `PaymentRequiredException`   |
| 403      | `PermissionDeniedException`  |
| 404      | `NotFoundException`          |
| 429      | `RateLimitException`         |
| other    | `FoPostException`            |

A `403` where `isSubscriptionRequired()` is true means the workspace has no active subscription;
read endpoints keep working without one.

## Scopes

An API key carries only the scopes granted when it was created, and every request is confined to
the workspaces that key can reach. `posts` also covers publishing, deliveries, and media; the rest
are `workspaces`, `accounts`, `labels`, `webhooks`, `analytics`, `automations`, `inbox`, and `ads`.
The ads calls that spend money (`boost`, `create`, `setStatus`, `delete`) need `publish` too.

## Example

[`examples/CreatePost.java`](examples/CreatePost.java) creates a post against a running API.

## Contributing

Issues and pull requests are welcome at [fopost/fopost-java](https://github.com/fopost/fopost-java).

```bash
mvn verify
```

Tests run against a fake transport and never touch the network.

## License

MIT

Questions or a problem: [fopost.com/contact](https://fopost.com/contact).
