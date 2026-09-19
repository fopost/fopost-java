package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.model.AccountGroup;
import com.fopost.sdk.param.CreatePostParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class AccountGroupsTest {

    private static final String GROUP = """
            {"data":{"id":"g1","name":"Brand A","account_ids":["a1","a2"],
                     "created_at":"2026-09-01T00:00:00Z","updated_at":"2026-09-02T00:00:00Z"}}""";

    @Test
    void listFiltersByWorkspace() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[{\"id\":\"g1\",\"name\":\"Brand A\",\"account_ids\":[\"a1\",\"a2\"]}]}");

        List<AccountGroup> groups = TestSupport.client(transport).accountGroups().list("w1");

        assertEquals("https://api.fopost.test/v1/account-groups?workspace_id=w1", transport.last().url());
        assertEquals(List.of("a1", "a2"), groups.get(0).accountIds());
    }

    @Test
    void createSendsWorkspaceNameAndMembers() {
        FakeTransport transport = new FakeTransport().enqueue(201, GROUP);

        AccountGroup group = TestSupport.client(transport).accountGroups().create("w1", "Brand A", List.of("a1", "a2"));

        assertEquals("POST", transport.last().method());
        assertEquals("{\"workspace_id\":\"w1\",\"name\":\"Brand A\",\"account_ids\":[\"a1\",\"a2\"]}",
                transport.lastBody());
        assertEquals("g1", group.id());
        assertEquals("2026-09-02T00:00:00Z", group.updatedAt().toString());
    }

    @Test
    void getUpdateDeleteAndSetMembersHitTheGroupPaths() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, GROUP)
                .enqueue(200, GROUP)
                .enqueue(200, "{\"message\":\"Account group deleted\"}")
                .enqueue(200, GROUP);
        FoPost client = TestSupport.client(transport);

        client.accountGroups().get("g1");
        assertEquals("https://api.fopost.test/v1/account-groups/g1", transport.last().url());

        client.accountGroups().update("g1", "Brand B");
        assertEquals("PATCH", transport.last().method());
        assertEquals("{\"name\":\"Brand B\"}", transport.lastBody());

        client.accountGroups().delete("g1");
        assertEquals("DELETE", transport.last().method());

        client.accountGroups().setMembers("g1", List.of("a3"));
        assertEquals("PUT", transport.last().method());
        assertEquals("https://api.fopost.test/v1/account-groups/g1/members", transport.last().url());
        assertEquals("{\"account_ids\":[\"a3\"]}", transport.lastBody());
    }

    @Test
    void accountsListCanFilterByGroup() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[{\"id\":\"a1\",\"name\":\"Shop\",\"platformName\":\"Acme\"}]}");

        var accounts = TestSupport.client(transport).accounts().list("w1", "g1");

        assertEquals("https://api.fopost.test/v1/accounts?workspaceId=w1&group_id=g1", transport.last().url());
        assertEquals("Acme", accounts.get(0).platformName());
    }

    @Test
    void renameSendsAnExplicitNullToRestoreThePlatformName() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"id\":\"a1\",\"name\":\"Acme\",\"platform_name\":\"Acme\"}}");

        var account = TestSupport.client(transport).accounts().rename("a1", null);

        assertEquals("PATCH", transport.last().method());
        assertEquals("https://api.fopost.test/v1/accounts/a1", transport.last().url());
        assertEquals("{\"display_name\":null}", transport.lastBody());
        assertEquals("Acme", account.platformName());
    }

    @Test
    void moveReturnsTheNewWorkspaceAndSurfacesBlockingTables() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"id\":\"a1\",\"workspace_id\":\"w2\"}}")
                .enqueue(409, "{\"error\":\"move_blocked\",\"message\":\"Blocked\",\"blocking_tables\":[\"posts\"]}");
        FoPost client = TestSupport.client(transport);

        assertEquals("w2", client.accounts().move("a1", "w2").workspaceId());
        assertEquals("https://api.fopost.test/v1/accounts/a1/move", transport.last().url());
        assertEquals("{\"workspace_id\":\"w2\"}", transport.lastBody());

        FoPostException error = assertThrows(FoPostException.class, () -> client.accounts().move("a1", "w2"));
        assertEquals(409, error.status());
        assertEquals("move_blocked", error.code());
        assertEquals("posts", ((JsonNode) error.body()).path("blocking_tables").get(0).asText());
    }

    @Test
    void createPostCanTargetAGroupWithoutAccounts() {
        var body = CreatePostParams.of("w1").accountGroupId("g1").content("Hi").toMap();

        assertEquals("g1", body.get("account_group_id"));
        assertFalse(body.containsKey("accounts"));
        assertTrue(CreatePostParams.of("w1").content("Hi").toMap().containsKey("accounts"));
    }
}
