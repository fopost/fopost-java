package com.fopost.sdk.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fopost.sdk.internal.ApiClient;
import com.fopost.sdk.internal.Json;
import com.fopost.sdk.model.Account;
import com.fopost.sdk.model.AccountAnalyticsHistory;
import com.fopost.sdk.model.AccountHealth;
import com.fopost.sdk.model.AccountValidation;
import com.fopost.sdk.model.AccountsHealthSummary;
import com.fopost.sdk.model.DiscordAck;
import com.fopost.sdk.model.DiscordChannel;
import com.fopost.sdk.model.DiscordIdentity;
import com.fopost.sdk.model.DiscordMember;
import com.fopost.sdk.model.DiscordMessage;
import com.fopost.sdk.model.DiscordMessageRef;
import com.fopost.sdk.model.DiscordRole;
import com.fopost.sdk.model.DiscordScheduledEvent;
import com.fopost.sdk.model.DiscordThread;
import com.fopost.sdk.model.MetaGreeting;
import com.fopost.sdk.model.MetaGreetingText;
import com.fopost.sdk.model.MetaIceBreaker;
import com.fopost.sdk.model.MetaIceBreakers;
import com.fopost.sdk.model.MetaPersistentMenu;
import com.fopost.sdk.model.MetaPersistentMenuEntry;
import com.fopost.sdk.model.SlackChannel;
import com.fopost.sdk.model.SlackIdentity;
import com.fopost.sdk.model.SlackMember;
import com.fopost.sdk.model.TelegramBotCommand;
import com.fopost.sdk.model.TelegramBotCommands;
import com.fopost.sdk.model.TelegramConnectCode;
import com.fopost.sdk.model.TelegramConnectStatus;
import com.fopost.sdk.model.TokenRefresh;
import com.fopost.sdk.model.WebhookSubscription;
import com.fopost.sdk.param.CreateAccountParams;
import com.fopost.sdk.param.DiscordEventParams;
import com.fopost.sdk.param.DiscordRoleParams;
import com.fopost.sdk.param.UpdateDiscordIdentityParams;
import com.fopost.sdk.param.UpdateSlackIdentityParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The social accounts connected to a workspace. */
public final class AccountsResource {

    private final ApiClient http;
    private final CommunitiesResource communities;

    public AccountsResource(ApiClient http) {
        this.http = http;
        this.communities = new CommunitiesResource(http);
    }

    /** X communities, per account. */
    public CommunitiesResource communities() {
        return communities;
    }

    /** Connected accounts across every workspace the key can reach. */
    public List<Account> list() {
        return list(null);
    }

    public List<Account> list(String workspaceId) {
        return list(workspaceId, null);
    }

    /** {@code groupId} keeps only the accounts in that account group. Either argument may be null. */
    public List<Account> list(String workspaceId, String groupId) {
        Map<String, Object> params = new LinkedHashMap<>();
        if (workspaceId != null) {
            params.put("workspaceId", workspaceId);
        }
        if (groupId != null) {
            params.put("group_id", groupId);
        }
        return http.convertList(ApiClient.unwrap(http.get("/v1/accounts", params.isEmpty() ? null : params)),
                Account.class);
    }

    public Account get(String accountId) {
        return http.convert(ApiClient.unwrap(http.get("/v1/accounts/" + accountId, null)), Account.class);
    }

    /** Connect an account with credentials you already hold, instead of the dashboard OAuth flow. */
    public Account create(CreateAccountParams params) {
        return http.convert(ApiClient.unwrap(http.post("/v1/accounts", params.toMap())), Account.class);
    }

    /**
     * Set the name shown instead of the platform name. {@code null} or an empty string restores the
     * platform name. Returns {@code id}, {@code name} and {@code platformName}.
     */
    public Account rename(String accountId, String displayName) {
        // An ObjectNode keeps an explicit null, which the shared mapper would drop from a Map.
        ObjectNode body = Json.MAPPER.createObjectNode().put("display_name", displayName);
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", "/v1/accounts/" + accountId, body, null)), Account.class);
    }

    /**
     * Move the account to another workspace the caller owns. Returns {@code id} and {@code workspaceId}.
     *
     * <p>A 409 is a {@code FoPostException}: code {@code move_blocked} carries {@code blocking_tables} on
     * {@code body()}; otherwise the target already has an account on that network.
     */
    public Account move(String accountId, String workspaceId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workspace_id", workspaceId);
        return http.convert(
                ApiClient.unwrap(http.post("/v1/accounts/" + accountId + "/move", body)), Account.class);
    }

    /** Disconnect the account. Posts already published stay where they are. */
    public void delete(String accountId) {
        http.delete("/v1/accounts/" + accountId);
    }

    /** Token validity for every account, with the counts rolled up. */
    public AccountsHealthSummary healthSummary() {
        return healthSummary(null);
    }

    public AccountsHealthSummary healthSummary(String workspaceId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/health", query("workspaceId", workspaceId))),
                AccountsHealthSummary.class);
    }

    /** Health for one account, as last recorded. */
    public AccountHealth health(String accountId) {
        return health(accountId, false);
    }

    /** {@code refresh} re-checks the credentials against the platform instead of reading the cache. */
    public AccountHealth health(String accountId, boolean refresh) {
        Map<String, Object> params = refresh ? query("refresh", "true") : null;
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/health", params)), AccountHealth.class);
    }

    /** Make this the account a post targets by default, or clear the flag. Returns the new state. */
    public boolean togglePrimary(String accountId) {
        JsonNode data = ApiClient.unwrap(http.post("/v1/accounts/" + accountId + "/primary", null));
        return data.path("isPrimary").asBoolean(false);
    }

    /** Check the stored credentials against the platform, now. */
    public AccountValidation validate(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/accounts/" + accountId + "/validate", null)), AccountValidation.class);
    }

    /** Force an OAuth token refresh, ahead of the automatic one. */
    public TokenRefresh refreshToken(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/accounts/" + accountId + "/refresh-token", null)), TokenRefresh.class);
    }

    /** Follower and reach history for the account, newest first. */
    public AccountAnalyticsHistory analytics(String accountId) {
        return analytics(accountId, null);
    }

    public AccountAnalyticsHistory analytics(String accountId, Integer limit) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/analytics", query("limit", limit))),
                AccountAnalyticsHistory.class);
    }

    /** Mint a Telegram connect code for the key's only workspace. */
    public TelegramConnectCode createTelegramConnectCode() {
        return createTelegramConnectCode(null);
    }

    /**
     * Mint a one-time code, valid for 15 minutes. Sending {@code /connect <code>} to the bot in a chat
     * connects that chat. {@code workspaceId} may be null for a key bound to one workspace.
     */
    public TelegramConnectCode createTelegramConnectCode(String workspaceId) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (workspaceId != null) {
            body.put("workspaceId", workspaceId);
        }
        return http.convert(
                ApiClient.unwrap(http.post("/v1/accounts/telegram/connect-code", body)), TelegramConnectCode.class);
    }

    /** Whether a connect code has been used yet, and the account it connected. */
    public TelegramConnectStatus getTelegramConnectStatus(String code) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/telegram/connect-code/status", query("code", code))),
                TelegramConnectStatus.class);
    }

    /** The command menu the bot shows in this Telegram chat. */
    public TelegramBotCommands getTelegramBotCommands(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/telegram/commands", null)),
                TelegramBotCommands.class);
    }

    /** Replace the command menu for this Telegram chat, 1-100 commands. */
    public TelegramBotCommands setTelegramBotCommands(String accountId, List<TelegramBotCommand> commands) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("commands", commands);
        return http.convert(
                ApiClient.unwrap(http.put("/v1/accounts/" + accountId + "/telegram/commands", body)),
                TelegramBotCommands.class);
    }

    /** Clear the command menu for this Telegram chat. */
    public TelegramBotCommands deleteTelegramBotCommands(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.delete("/v1/accounts/" + accountId + "/telegram/commands")),
                TelegramBotCommands.class);
    }

    /**
     * Channels the Slack app can post to: every public channel, and private ones it was invited to.
     *
     * <p>A 409 {@code webhook_connection} means the account posts through a webhook; reconnect it with
     * the Slack app. The same applies to the other Slack calls.
     */
    public List<SlackChannel> listSlackChannels(String accountId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/slack/channels", null)),
                SlackChannel.class);
    }

    /** People in the connected Slack workspace, for addressing a DM. */
    public List<SlackMember> listSlackMembers(String accountId) {
        return http.convertList(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/slack/members", null)),
                SlackMember.class);
    }

    /** The name and icon this Slack account posts under. */
    public SlackIdentity getSlackIdentity(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/slack/identity", null)),
                SlackIdentity.class);
    }

    /** Change the name or icon this Slack account posts under. */
    public SlackIdentity updateSlackIdentity(String accountId, UpdateSlackIdentityParams params) {
        return http.convert(
                ApiClient.unwrap(http.request(
                        "PATCH", "/v1/accounts/" + accountId + "/slack/identity", params.toJson(), null)),
                SlackIdentity.class);
    }

    // ─── Meta messaging settings (Facebook Pages, Instagram) ─────────

    /** The prompts shown before the first message. A network without them answers 400. */
    public MetaIceBreakers getIceBreakers(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/messaging/ice-breakers", null)),
                MetaIceBreakers.class);
    }

    /** Replace the ice breakers, up to four. */
    public MetaIceBreakers setIceBreakers(String accountId, List<MetaIceBreaker> iceBreakers) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ice_breakers", iceBreakers);
        return http.convert(
                ApiClient.unwrap(http.put("/v1/accounts/" + accountId + "/messaging/ice-breakers", body)),
                MetaIceBreakers.class);
    }

    /** Clear the ice breakers. */
    public MetaIceBreakers deleteIceBreakers(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.delete("/v1/accounts/" + accountId + "/messaging/ice-breakers")),
                MetaIceBreakers.class);
    }

    /** The always-visible Messenger menu. Facebook Pages only; other networks answer 400. */
    public MetaPersistentMenu getPersistentMenu(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/messaging/persistent-menu", null)),
                MetaPersistentMenu.class);
    }

    /** Replace the menu, one entry per locale, up to three items each. */
    public MetaPersistentMenu setPersistentMenu(String accountId, List<MetaPersistentMenuEntry> menu) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("persistent_menu", menu);
        return http.convert(
                ApiClient.unwrap(http.put("/v1/accounts/" + accountId + "/messaging/persistent-menu", body)),
                MetaPersistentMenu.class);
    }

    /** Clear the menu. */
    public MetaPersistentMenu deletePersistentMenu(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.delete("/v1/accounts/" + accountId + "/messaging/persistent-menu")),
                MetaPersistentMenu.class);
    }

    /** The text shown before a Messenger conversation starts. Facebook Pages only. */
    public MetaGreeting getGreeting(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/messaging/greeting", null)),
                MetaGreeting.class);
    }

    /** Replace the greeting, one entry per locale, each up to 160 characters. */
    public MetaGreeting setGreeting(String accountId, List<MetaGreetingText> greeting) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("greeting", greeting);
        return http.convert(
                ApiClient.unwrap(http.put("/v1/accounts/" + accountId + "/messaging/greeting", body)),
                MetaGreeting.class);
    }

    /** Clear the greeting. */
    public MetaGreeting deleteGreeting(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.delete("/v1/accounts/" + accountId + "/messaging/greeting")),
                MetaGreeting.class);
    }

    /** What the network is delivering to the FoPost webhook for this account. */
    public WebhookSubscription getWebhookSubscription(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.get("/v1/accounts/" + accountId + "/webhook-subscription", null)),
                WebhookSubscription.class);
    }

    /** Subscribe to every field this account needs, lapsed or not. */
    public WebhookSubscription resubscribeWebhook(String accountId) {
        return http.convert(
                ApiClient.unwrap(http.post("/v1/accounts/" + accountId + "/webhook-subscription", null)),
                WebhookSubscription.class);
    }

    // ── Discord (bot connections) ───────────────────────────────────────

    /**
     * Text channels the bot can post to in the connected server.
     *
     * <p>A 409 {@code webhook_connection} means the account posts through a webhook; upgrade it to
     * the bot first. The same applies to every other Discord call here.
     */
    public List<DiscordChannel> listDiscordChannels(String accountId) {
        return http.convertList(ApiClient.unwrap(http.get(discord(accountId, "/channels"), null)), DiscordChannel.class);
    }

    /** Move the account to another channel in the same server. */
    public DiscordChannel switchDiscordChannel(String accountId, String channelId) {
        ObjectNode body = Json.MAPPER.createObjectNode().put("channel_id", channelId);
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", discord(accountId, "/channels/current"), body, null)),
                DiscordChannel.class);
    }

    /** The nickname and avatar the bot wears in the server. */
    public DiscordIdentity getDiscordIdentity(String accountId) {
        return http.convert(ApiClient.unwrap(http.get(discord(accountId, "/identity"), null)), DiscordIdentity.class);
    }

    /** Change the nickname or avatar the bot wears in the server. */
    public DiscordIdentity updateDiscordIdentity(String accountId, UpdateDiscordIdentityParams params) {
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", discord(accountId, "/identity"), params.toJson(), null)),
                DiscordIdentity.class);
    }

    /** Pinned messages in the account's channel. */
    public List<DiscordMessage> listDiscordPins(String accountId) {
        return http.convertList(
                ApiClient.unwrap(http.get(discord(accountId, "/messages/pinned"), null)), DiscordMessage.class);
    }

    /** Remove a message from the account's channel. */
    public DiscordAck deleteDiscordMessage(String accountId, String messageId) {
        return ack(http.delete(discord(accountId, "/messages/" + messageId)));
    }

    /** Pin a message in the account's channel. */
    public DiscordAck pinDiscordMessage(String accountId, String messageId) {
        return ack(http.post(discord(accountId, "/messages/" + messageId + "/pin"), null));
    }

    /** Unpin a message in the account's channel. */
    public DiscordAck unpinDiscordMessage(String accountId, String messageId) {
        return ack(http.delete(discord(accountId, "/messages/" + messageId + "/pin")));
    }

    /** Publish an announcement-channel message to every server following the channel. */
    public DiscordMessageRef crosspostDiscordMessage(String accountId, String messageId) {
        return http.convert(
                ApiClient.unwrap(http.post(discord(accountId, "/messages/" + messageId + "/crosspost"), null)),
                DiscordMessageRef.class);
    }

    /** Start a thread on a message. */
    public DiscordThread createDiscordThread(String accountId, String messageId, String name) {
        return createDiscordThread(accountId, messageId, name, null);
    }

    /** {@code autoArchiveDuration} is 60, 1440, 4320 or 10080 minutes, or null for the default. */
    public DiscordThread createDiscordThread(
            String accountId, String messageId, String name, Integer autoArchiveDuration) {
        ObjectNode body = Json.MAPPER.createObjectNode().put("name", name);
        if (autoArchiveDuration != null) {
            body.put("auto_archive_duration", autoArchiveDuration);
        }
        return http.convert(
                ApiClient.unwrap(http.post(discord(accountId, "/messages/" + messageId + "/thread"), body)),
                DiscordThread.class);
    }

    /** Send one message to a member of the server. */
    public DiscordMessageRef sendDiscordDm(String accountId, String memberId, String content) {
        ObjectNode body = Json.MAPPER.createObjectNode();
        body.put("member_id", memberId);
        body.put("content", content);
        return http.convert(
                ApiClient.unwrap(http.post(discord(accountId, "/dm"), body)), DiscordMessageRef.class);
    }

    /** The server's scheduled events. */
    public List<DiscordScheduledEvent> listDiscordEvents(String accountId) {
        return http.convertList(
                ApiClient.unwrap(http.get(discord(accountId, "/events"), null)), DiscordScheduledEvent.class);
    }

    /** One scheduled event. */
    public DiscordScheduledEvent getDiscordEvent(String accountId, String eventId) {
        return http.convert(
                ApiClient.unwrap(http.get(discord(accountId, "/events/" + eventId), null)),
                DiscordScheduledEvent.class);
    }

    /** Add an event to the server's calendar. */
    public DiscordScheduledEvent createDiscordEvent(String accountId, DiscordEventParams params) {
        return http.convert(
                ApiClient.unwrap(http.post(discord(accountId, "/events"), params.toJson())),
                DiscordScheduledEvent.class);
    }

    /** Change a scheduled event. */
    public DiscordScheduledEvent updateDiscordEvent(String accountId, String eventId, DiscordEventParams params) {
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", discord(accountId, "/events/" + eventId), params.toJson(), null)),
                DiscordScheduledEvent.class);
    }

    /** Remove a scheduled event. */
    public DiscordAck deleteDiscordEvent(String accountId, String eventId) {
        return ack(http.delete(discord(accountId, "/events/" + eventId)));
    }

    /** The server's roster. */
    public List<DiscordMember> listDiscordMembers(String accountId) {
        return listDiscordMembers(accountId, null, null);
    }

    /** {@code query} searches by username or nickname prefix. Either argument may be null. */
    public List<DiscordMember> listDiscordMembers(String accountId, String query, Integer limit) {
        Map<String, Object> params = new LinkedHashMap<>();
        if (query != null) {
            params.put("q", query);
        }
        if (limit != null) {
            params.put("limit", limit);
        }
        return http.convertList(
                ApiClient.unwrap(http.get(discord(accountId, "/members"), params.isEmpty() ? null : params)),
                DiscordMember.class);
    }

    /** One member of the server. */
    public DiscordMember getDiscordMember(String accountId, String memberId) {
        return http.convert(
                ApiClient.unwrap(http.get(discord(accountId, "/members/" + memberId), null)), DiscordMember.class);
    }

    /** The server's roles, highest first. */
    public List<DiscordRole> listDiscordRoles(String accountId) {
        return http.convertList(ApiClient.unwrap(http.get(discord(accountId, "/roles"), null)), DiscordRole.class);
    }

    /** Add a role to the server. */
    public DiscordRole createDiscordRole(String accountId, DiscordRoleParams params) {
        return http.convert(ApiClient.unwrap(http.post(discord(accountId, "/roles"), params.toJson())), DiscordRole.class);
    }

    /** Change a role on the server. */
    public DiscordRole updateDiscordRole(String accountId, String roleId, DiscordRoleParams params) {
        return http.convert(
                ApiClient.unwrap(http.request("PATCH", discord(accountId, "/roles/" + roleId), params.toJson(), null)),
                DiscordRole.class);
    }

    /** Remove a role from the server. */
    public DiscordAck deleteDiscordRole(String accountId, String roleId) {
        return ack(http.delete(discord(accountId, "/roles/" + roleId)));
    }

    /** Give a member a role. */
    public DiscordAck addDiscordMemberRole(String accountId, String roleId, String memberId) {
        return ack(http.request("PUT", discord(accountId, "/roles/" + roleId + "/members/" + memberId), null, null));
    }

    /** Take a role from a member. */
    public DiscordAck removeDiscordMemberRole(String accountId, String roleId, String memberId) {
        return ack(http.delete(discord(accountId, "/roles/" + roleId + "/members/" + memberId)));
    }

    private static String discord(String accountId, String suffix) {
        return "/v1/accounts/" + accountId + "/discord" + suffix;
    }

    private DiscordAck ack(JsonNode body) {
        return http.convert(ApiClient.unwrap(body), DiscordAck.class);
    }

    private static Map<String, Object> query(String key, Object value) {
        if (value == null) {
            return null;
        }
        Map<String, Object> query = new LinkedHashMap<>();
        query.put(key, value);
        return query;
    }
}
