# Changelog

All notable changes to this project are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project uses
[Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added

- `blog()`: articles for a website. `getSettings`, `updateSettings`, `rotateWebsiteSecret`
  and `testWebsite`; `listKeywords`, `planKeywords`, `addKeyword`, `updateKeyword` and
  `deleteKeyword`; `listArticles`, `requestArticle`, `getArticle`, `updateArticle`,
  `regenerateArticle`, `publishArticle`, `rejectArticle` and `deleteArticle`, with the
  `BlogSettings`, `BlogKeyword`, `BlogArticle`, `BlogArticleFull` and `BlogArticleImage`
  records and the `BlogArticleStatus`, `BlogKeywordStatus` and `BlogTargetKind` constants.
  Reads need `posts`; planning, requesting and regenerating spend AI credits and need `ai`;
  publishing needs `publish`.
- `WebhookVerifier.verify` checks a delivery's signature over the raw bytes in constant time,
  preferring the timestamped `X-FoPost-Signature-256` within 300 seconds and falling back to
  `X-FoPost-Signature` when no timestamp is sent, then returns the parsed `WebhookEnvelope`.
  Also reachable as `webhooks().verify`. `WebhookEvents.ARTICLE_PUBLISHED` and the
  `BlogArticlePublished` record type its payload.
- `media().limits(workspaceId)` reads `GET /v1/media/limits` into `UploadLimits`: the account's
  largest file by any route (100 MB up to 2 connected accounts, 400 MB up to 7, 2 GB up to 15,
  5 GB beyond or with the Large Uploads add-on), the per-file multipart ceiling, the next tier,
  and the allowed types.
- `posts().create(params, files)` and `posts().update(id, params, files)` attach `FileInput`s to
  the first content block. Files within the multipart ceiling ride in one `multipart/form-data`
  request (`post` JSON field plus `files` parts); larger ones are uploaded directly and attached
  as media items before the usual JSON request. An update without new content reads the post
  first and appends to its first block.
- `FileInput` describes a file to upload from a `Path`, an `InputStream` with a known size, or a
  byte array. `media().uploadDirect(workspaceId, FileInput)` and `uploadDirect(PresignUploadParams,
  FileInput)` stream it to the upload URL rather than holding it in memory.
- `PresignUploadParams` takes `folderId`, `origin`, `brandRole`, `brandVoiceId` and
  `personConsent`; `PresignedUpload.maxFileBytes` reports the ceiling at reservation time.

- `InboxItem.moderationStatus` carries the platform's own state for a comment
  (`published`, `held`, `spam`, `rejected`), and `InboxAccount.reconnectRequired`
  flags an account connected before the inbox asked for a permission it needs.

- `broadcasts()`: one message into every conversation the workspace already has with a
  segment of its contacts. `list`, `get`, `create`, `update`, `delete`, `send`, `cancel`
  and `recipients`. Reading needs the `inbox` scope; `send` and `cancel` also need `publish`.
- `sequences()`: a series of messages on a delay. `list`, `get`, `create`, `update`,
  `delete`, `enroll`, `unenroll` and `enrollments`. `enroll` and `unenroll` need `publish`.
- Both honour each network's messaging window server-side. Messenger and Instagram take a
  business-initiated message only within 24 hours of the contact's last one, so recipients
  outside it come back `skipped` with `skip_reason` `window_closed` and nothing is
  attempted — the number sent is often lower than the audience.

- `contacts()`: the people behind the inbox. `list`, `get`, `create`, `update`, `delete`,
  `conversations` (the threads one person appears in), `importCsv`, and
  `listFields`/`createField`/`updateField`/`updateFieldOptions`/`deleteField` for the
  custom columns a workspace keeps. All need the `inbox` scope.
- `contacts().conversationAnalytics` reads `/v1/analytics/inbox/conversations`: volume and
  median reply time per thread. Needs the `analytics` scope.
- Meta messaging settings on `accounts()`: `getIceBreakers`, `setIceBreakers` and
  `deleteIceBreakers` (Facebook Pages and Instagram), plus `getPersistentMenu`,
  `setPersistentMenu`, `deletePersistentMenu`, `getGreeting`, `setGreeting` and
  `deleteGreeting` (Facebook Pages). A network without a field answers 400.
- `accounts().getWebhookSubscription` reports whether the network is still delivering events
  for an account, and `resubscribeWebhook` puts a lapsed subscription back.
- `inbox().passThreadControl`, `takeThreadControl` and `handover` move a Messenger thread
  between Meta apps (`inbox` scope, plus `publish`).
- `knowledge()`: the workspace knowledge base — `list`, `create` (plus the
  `createText`, `createUrl` and `createFile` shorthands), `update`, `delete`,
  `sync` and `search`, with the `KnowledgeSource` and `KnowledgeMatch` records.
  A source is an FAQ, a note, a URL on your own site or a plain-text/CSV media
  item; `search` returns the passages closest to a question, and is what grounds
  a drafted inbox reply in your own answers. Needs the `inbox` scope.
- `Platforms.SNAPCHAT`, included in `Platforms.ALL`.

- `accounts()`: `listSlackChannels`, `listSlackMembers`, `getSlackIdentity` and
  `updateSlackIdentity` (`UpdateSlackIdentityParams`) for a Slack account. A webhook-connected
  account answers 409 `webhook_connection`.

### Changed

- Presign and direct upload accept sizes up to 5 GB; there is no 50 MB ceiling in the SDK.
- An oversize file is a `413` with code `file_too_large`. It surfaces as a `FoPostException`
  with `status()` 413 and `max_file_bytes`, `file_bytes` and `upgrade_url` on `body()`.
- Multipart bodies and direct uploads stream from their source. `HttpRequestData` gained
  `bodyStream` and `bodyLength` beside `body`; a custom `Transport` must send whichever is set.

## [0.3.0] - 2026-09-19

### Added

- `ads()`: the campaign tree and its objects, read live from Meta: `accountTree`, and
  create/get/update/delete/duplicate for campaigns, ad sets and network ads, plus `bulkSetStatus`.
  The writes need the `publish` scope as well as `ads`.
- `ads()`: `creatives`, `createCreative` (image, video or carousel), `creative`, `deleteCreative`;
  `audience`, `updateAudience`, `deleteAudience`, `addAudienceUsers`; `estimateReach`; `insights`
  and `adInsights` with an optional breakdown and daily timeline.
- `ads()`: `leadForm`, `archiveLeadForm`, the cursor-paged `leadsFeed`, and `leadPages`,
  `subscribeLeadPage`, `unsubscribeLeadPage`.
- `CreateAdParams.urlTags` and `Ad.Creative.urlTags`.

- `inbox()`: `like`, `unlike`, `pin`, `unpin`, `react`, `editComment`, `startConversation`
  (`StartConversationParams`), `setTyping`, and `reply` with `InboxReplyParams` for media and quick
  replies. All of them also need the `publish` scope, as does deleting our own reply.
- `InboxItem` gains `liked`, `pinned`, `reaction`, `editedAt` and the `canLike`, `canPin`,
  `canEdit`, `canReact`, `canSendMedia`, `canQuickReply` and `canPrivateReply` flags;
  `InboxAccount` gains `canStartConversation`.
- `validate` resource (`client.validate()`): `post`, `length` and `media` check a draft, a text
  length, or a media url against the platform rules without creating a post. Needs the `posts`
  scope.

## [0.2.0] - 2026-09-19

### Added

- `inbox` resource (`client.inbox()`): comments, mentions and direct messages, thread and
  conversation lists, unread count, mark read, refresh, update, reply, hide, unhide, delete, and
  the reply approval queue. Needs the `inbox` scope.
- `ads` resource (`client.ads()`): boosts, standalone ads, ad connections and sources, audiences,
  targeting search, lead forms and leads. Needs the `ads` scope; `boost`, `create`, `setStatus`
  and `delete` also need `publish`.

## [0.1.0] - 2026-08-30

### Added

- Initial release: `posts`, `accounts`, `workspaces`, `labels`, `webhooks`, `analytics`,
  `automations`, `media` and `ai` over `java.net.http`, with a 429 retry loop and a typed
  exception per error status.
