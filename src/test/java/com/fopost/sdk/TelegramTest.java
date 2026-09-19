package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.TelegramBotCommand;
import com.fopost.sdk.model.TelegramBotCommands;
import com.fopost.sdk.model.TelegramConnectCode;
import com.fopost.sdk.model.TelegramConnectStatus;
import java.util.List;
import org.junit.jupiter.api.Test;

class TelegramTest {

    private static final String COMMANDS = """
            {"data":{"commands":[{"command":"start","description":"Start the bot"}]}}""";

    @Test
    void createConnectCodeSendsTheWorkspaceAndMapsTheLinks() {
        FakeTransport transport = new FakeTransport()
                .enqueue(201, """
                        {"data":{"code":"ABC123","command":"/connect ABC123","bot_username":"fopost_bot",
                                 "deep_link":"https://t.me/fopost_bot?start=ABC123","group_link":null,
                                 "expires_at":"2026-09-19T12:15:00Z"}}""")
                .enqueue(201, "{\"data\":{\"code\":\"X\",\"command\":\"/connect X\",\"expires_at\":\"2026-09-19T12:15:00Z\"}}");
        FoPost client = TestSupport.client(transport);

        TelegramConnectCode code = client.accounts().createTelegramConnectCode("w1");

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/telegram/connect-code", transport.last().url());
        assertEquals("{\"workspaceId\":\"w1\"}", transport.lastBody());
        assertEquals("fopost_bot", code.botUsername());
        assertEquals("https://t.me/fopost_bot?start=ABC123", code.deepLink());
        assertNull(code.groupLink());
        assertEquals("2026-09-19T12:15:00Z", code.expiresAt().toString());

        client.accounts().createTelegramConnectCode();
        assertEquals("{}", transport.lastBody());
    }

    @Test
    void connectStatusQueriesTheCode() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"status\":\"failed\",\"account_id\":null,\"reason\":\"card_required\"}}");

        TelegramConnectStatus status = TestSupport.client(transport).accounts().getTelegramConnectStatus("ABC123");

        assertEquals("https://api.fopost.test/v1/accounts/telegram/connect-code/status?code=ABC123",
                transport.last().url());
        assertEquals("failed", status.status());
        assertNull(status.accountId());
        assertEquals("card_required", status.reason());
    }

    @Test
    void botCommandsGetSetAndDeleteHitTheAccountPath() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, COMMANDS)
                .enqueue(200, COMMANDS)
                .enqueue(200, "{\"data\":{\"commands\":[]}}");
        FoPost client = TestSupport.client(transport);

        TelegramBotCommands got = client.accounts().getTelegramBotCommands("a1");
        assertEquals("GET", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/telegram/commands", transport.last().url());
        assertEquals(new TelegramBotCommand("start", "Start the bot"), got.commands().get(0));

        client.accounts().setTelegramBotCommands("a1", List.of(new TelegramBotCommand("start", "Start the bot")));
        assertEquals("PUT", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/telegram/commands", transport.last().url());
        assertEquals("{\"commands\":[{\"command\":\"start\",\"description\":\"Start the bot\"}]}",
                transport.lastBody());

        TelegramBotCommands cleared = client.accounts().deleteTelegramBotCommands("a1");
        assertEquals("DELETE", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1/telegram/commands", transport.last().url());
        assertTrue(cleared.commands().isEmpty());
    }
}
