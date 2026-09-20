# CLAUDE.md

Guidance for Claude Code (claude.ai/code) when working in this repository.

## What This Is

`com.fopost:fopost-java` — the official Java SDK for the FoPost REST API, destined for
**Maven Central** (via the Sonatype Central Portal). It wraps the HTTP API in an immutable,
thread-safe `com.fopost.sdk.FoPost` client with typed resources, record models, and an
exception per error status.

- **Java 17** (`maven.compiler.release`), built with Maven, packaged as a jar with attached
  sources and javadoc.
- Runtime dependencies: `jackson-databind` and `jackson-datatype-jsr310` (2.18.2). HTTP is the
  JDK's own `java.net.http.HttpClient` — no HTTP library dependency.
- Version `0.3.0` in `pom.xml`, mirrored by the constant in
  `src/main/java/com/fopost/sdk/internal/Version.java` (the release workflow enforces the match).

## Downstream Packages

These repos wrap this SDK and must be updated in lockstep:

- `fopost-spring` — `fopost-spring-boot-starter`: auto-configuration, properties, and Spring
  ergonomics over this SDK

**Whenever you change this SDK's public surface — a renamed method, a changed parameter,
a new or removed resource, a new error type, a bumped minimum language version — you must
open a matching PR in every repo listed above in the same session.** They are separate
git repos, checked out as siblings at `../fopost-<child>`. A parent release that silently
breaks a child is only discovered by the user who upgrades first.

Also bump the child's dependency constraint on this package and note the change in its
CHANGELOG when this package is released.

## Brand Rules

- The product is **FoPost** (`fopost.com`). Never write "OwlStack" — retired Aug 2026.
- Never write an email address anywhere: not in code, javadoc, README, or `pom.xml`.
  Support is https://fopost.com/contact and the GitHub issues page.
- Never name AI providers or models, infrastructure vendors, hosting, or any person.
  `<developers>` names the brand and Porter Bridge, LLC — keep it that way.

## Architecture

```
src/main/java/com/fopost/sdk/
  FoPost.java              entry point + Builder; owns one instance per resource
  FoPostException.java     base error, plus Validation/Authentication/PaymentRequired/
                           PermissionDenied/NotFound/RateLimit siblings in the same package
  internal/
    Transport.java         the seam: @FunctionalInterface send(HttpRequestData) -> HttpResponseData
    JdkTransport.java      default implementation over java.net.http
    ApiClient.java         headers, query encoding, JSON coding, retry loop, decode, unwrap
    HttpRequestData/HttpResponseData   records; response header lookup is case-insensitive
    Json.java              the shared ObjectMapper
    Multipart.java         multipart body builder, used by media upload
    Sleeper.java           @FunctionalInterface test seam for the retry wait
    Version.java           the version compiled into the User-Agent
  model/                   ~85 response records (Post, Account, Workspace, Page, PageMeta, InboxItem, Ad, …)
  param/                   request builders (CreatePostParams, PostListParams, …)
  resource/                PostsResource, AccountsResource (+ .communities()), WorkspacesResource,
                           LabelsResource, WebhooksResource, AnalyticsResource,
                           AutomationsResource, MediaResource, AiResource, CommunitiesResource,
                           InboxResource, AdsResource, ValidateResource
```

**Request flow.** `client.posts().create(params)` → `PostsResource` builds the body and calls
`ApiClient.post("/v1/posts", body)` → `ApiClient.request` picks the encoding (JSON, or
`Multipart` when the body is one) → `send` assembles headers, builds the URL, and enters the
retry loop → `Transport.send(HttpRequestData)` returns `HttpResponseData` → `decode` reads the
tree with Jackson and either throws via `errorFor` or returns the `JsonNode` → the resource
calls `ApiClient.unwrap(...)` and `convert(...)`/`convertList(...)` into a record.

- **`com.fopost.sdk.internal.Transport` is the seam.** It is a `@FunctionalInterface`; pass an
  implementation to `FoPost.builder().transport(...)` to route through a proxy, an existing HTTP
  stack, or a fake. `Builder.sleeper(...)` is package-private and exists only for the retry tests.
- Resource classes take the full versioned path (`"/v1/posts"`) — the base URL has no path
  segment. Keep that convention when adding an endpoint.
- Model types are Jackson-mapped records; `param/` types are fluent builders.
- `FoPost` instances are immutable and safe to share across threads.

**Resources wired today:** `posts`, `accounts` (with `accounts().communities()`), `workspaces`,
`labels`, `webhooks`, `analytics`, `automations`, `media`, `ai`, `inbox`, `contacts`, `ads`, `validate`. This is
the most complete of the FoPost SDKs — do not narrow it. `FoPost.request(...)` is the escape hatch for
anything unwrapped.

- `contacts` (scope `inbox`) covers `/v1/contacts/*`: the CRUD, `import`,
  `{id}/conversations` and the `/v1/contacts/fields` family. Its list envelope is
  `{data, pagination}`, so it decodes into `ContactPage` rather than `Page<T>`.
  `createField` puts the workspace on the query string because the handler reads it there.
  `ContactParams.Update` serializes its `fields` through an `ObjectNode`: the mapper is
  configured `NON_NULL`, so a plain map would drop the null that clears a field.
  `conversationAnalytics` reaches `/v1/analytics/inbox/conversations` and needs `analytics`.
- `inbox` (scope `inbox`) covers `/v1/inbox/*` except `/v1/inbox/chat/*` (browser-encrypted X
  Chat) and `/v1/inbox/{id}/attachments/{index}` (a binary stream; this SDK has no download
  pattern). Its lists carry `meta: { page, perPage, total }`, mapped by `model/InboxPage` +
  `InboxPageMeta`, not the `PageMeta` the other lists use. `POST /inbox/read` and `/inbox/refresh`
  take snake_case bodies; `PATCH /inbox/{id}` takes `{ state, snoozedUntil }`.
- `ads` (scope `ads`) covers `/v1/ads/*`. Request bodies are camelCase; `boost`, `create`,
  `setStatus` and `delete` also need the `publish` scope, and a boost or ad starts paused unless
  `paused` is false. Say both in the javadoc of anything new that spends.

## API Contract

- **Base URL:** `ApiClient.DEFAULT_BASE_URL` = `https://api.fopost.com` — **host only, no version
  path.** Every resource path therefore starts `/v1/...`. Only trailing slashes are stripped from
  an override. There is **no `FOPOST_BASE_URL` env read**; use `builder().baseUrl(...)`.
- **Auth:** header `X-API-Key: <key>`, never Bearer. The key falls back to the `FOPOST_API_KEY`
  environment variable (`FoPost.API_KEY_ENV`); a missing key is an `IllegalArgumentException`
  from `Builder.build()`, before any request.
- **Headers on every request:** `Accept: application/json`, `X-API-Key`,
  `User-Agent: fopost-java/<Version.VALUE>`, and `Content-Type` only when there is a body
  (`application/json`, or the multipart type when uploading).
- **Timeout:** `Duration.ofSeconds(30)` default, applied by `JdkTransport`.
- **Retries — as implemented here:** `maxRetries` (default 3) is the **total attempt count**, and
  only **HTTP 429** is retried. 5xx is not retried. A network `IOException` is wrapped in a
  `FoPostException` and thrown on the first attempt; an `InterruptedException` re-sets the thread
  interrupt flag and throws — a cancelled request is never retried. There is **no exponential
  backoff**: the wait is `Retry-After` (delta-seconds, or an RFC 1123 date) when present,
  otherwise a flat 1 second, capped at `MAX_RETRY_WAIT` = 60 seconds.
- **Success envelope:** `ApiClient.unwrap(JsonNode)` peels `{"data": ...}` only when the key is
  present, because some endpoints (create a post, get a post, labels) answer bare. Paginated lists
  carry a sibling `meta` (`current_page`, `per_page`, `total`, `last_page`, `from`, `to`) mapped
  by `model/PageMeta`.
- **Error envelope:** `{"error": "<code>", "message": "<text>"}` maps onto
  `FoPostException.code()` and `getMessage()`; the decoded `JsonNode` stays on `body()` so callers
  can read extra fields such as `upgrade_url` on a 402. `toString()` renders
  `[<status> (<code>)] <message>`.
- **Exception map** (`ApiClient.errorFor`): 400/422 `ValidationException` ·
  401 `AuthenticationException` · 402 `PaymentRequiredException` · 403 `PermissionDeniedException` ·
  404 `NotFoundException` · 429 `RateLimitException` (carries a `Duration` retryAfter) ·
  **everything else, 5xx included, falls back to `FoPostException`** — there is no dedicated
  server-error class. All are unchecked (`extends RuntimeException`); `status()` is `0` for a
  transport or decoding failure.
- **Rate-limit headers** (`X-RateLimit-Limit`/`-Remaining`/`-Reset`) are not surfaced on models or
  exceptions. They are readable via `HttpResponseData.header(...)` from a custom `Transport`.

## Commands

```bash
mvn -B verify                        # compile, test, sources jar, javadoc jar — what CI runs
mvn -B test                          # tests only
mvn -B test -Dtest=RetryTest         # one test class
mvn -q install -DskipTests           # install 0.3.0 locally, so ../fopost-spring can resolve it
mvn -B -Prelease deploy -DskipTests  # GPG-sign and publish to Central (release workflow only)
```

CI (`.github/workflows/ci.yml`) runs `mvn -B verify` on Java **17, 21 and 25**, on push to `main`,
on PRs, and on manual dispatch.

## Conventions

- **There is no Checkstyle, Spotless, or formatter config in this repo** — style is held by review
  and by the compiler. `maven-compiler-plugin` runs with `-Xlint:all` and `-parameters`; keep the
  build warning-free.
- The existing source is Google-Java-Format shaped: 4-space indent, ~110 column lines, one class
  per file, imports fully expanded (no wildcards) and alphabetised. Match it.
- Public API is javadoc'd, including a `<pre>{@code …}</pre>` usage block on the client and the
  main resources. Internals get a short "why" comment or nothing. No narrated javadoc on obvious
  members.
- Response types are `record`s in `model/`; request types are fluent builders in `param/`.
  Anything under `internal/` is not API, even though it is `public` for cross-package access.
- `Version.VALUE` must be bumped alongside `<version>` in `pom.xml` — it is compiled into the
  User-Agent, and a stale value is invisible until someone reads a server log.

## Testing

- **JUnit 5** (`junit-jupiter` 5.11.4), tests in `src/test/java/com/fopost/sdk/`, run by surefire
  under `mvn verify`.
- **`FakeTransport implements Transport`.** It serves canned responses from a queue
  (`enqueue(status, body[, headers])`), records every `HttpRequestData`, and throws when a request
  has no queued response — so an unintended call fails the test rather than reaching the network.
- `TestSupport.client(...)` wires a `FoPost` around that transport with base URL
  `https://api.fopost.test`, an API key of `fp_test`, and a no-op or recording `Sleeper`, so
  `RetryTest` asserts the exact waits with no clock involved.
- **Tests never hit the live API.** No network call in the suite, ever, in CI or locally. If a
  change cannot be tested through `FakeTransport`, the change is in the wrong layer.

## Releasing

**`com.fopost:fopost-java` is NOT yet on Maven Central.** `.github/workflows/release.yml` exists
and is ready: it triggers on a `v*` tag (or manual dispatch), runs in the `central` GitHub
environment, verifies the tag matches `pom.xml` **and** that `Version.java` matches, runs
`mvn -B verify`, then publishes with `mvn -B -Prelease deploy -DskipTests` through the
`central-publishing-maven-plugin` (`autoPublish=true`) with GPG signing.

**Repo secrets referenced by `release.yml`** (all on the `central` environment):

- `MAVEN_CENTRAL_USERNAME`
- `MAVEN_CENTRAL_TOKEN`
- `GPG_PRIVATE_KEY`
- `GPG_PASSPHRASE`

First publish also requires, outside GitHub:

1. **Namespace verification for `com.fopost`** on the Sonatype Central Portal (a DNS TXT record on
   `fopost.com`). Nothing can be published until it is verified.
2. A **user token** generated on the Central Portal — that pair is what
   `MAVEN_CENTRAL_USERNAME` / `MAVEN_CENTRAL_TOKEN` hold, not the portal login.
3. A **GPG key pair** whose public key is published to a public keyserver; the ASCII-armoured
   private key goes in `GPG_PRIVATE_KEY` and its passphrase in `GPG_PASSPHRASE`. Central rejects
   unsigned artifacts.
4. The `central` environment created in GitHub repo settings (it gates who can trigger a release).

`CHANGELOG.md` follows Keep a Changelog; add an entry under the new version with every release.

## Git

- **Conventional Commits** `<type>(<scope>): <description>` — one logical change per commit.
- Branch `feature/<description>` off a fresh `main`; merge to `main` via PR.
- **Never run `gh pr create`.** Push the branch and hand over the compare link:
  `https://github.com/fopost/fopost-java/compare/main...<branch>`
- Never `git stash` — use a worktree for parallel work.
