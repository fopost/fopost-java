# Changelog

All notable changes to this project are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project uses
[Semantic Versioning](https://semver.org/).

## [Unreleased]

## [0.3.0] - 2026-09-19

### Added

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
