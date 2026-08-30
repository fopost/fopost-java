package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.Webhook;
import com.fopost.sdk.model.WebhookEvents;
import com.fopost.sdk.param.AnalyticsParams;
import com.fopost.sdk.param.CaptionParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class ResourcesTest {

    @Test
    void accountsListReadsTheCamelCaseQueryParameter() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":[]}");

        TestSupport.client(transport).accounts().list("w1");

        assertEquals("https://api.fopost.test/v1/accounts?workspaceId=w1", transport.last().url());
    }

    @Test
    void accountHealthCanForceARefresh() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"id\":\"a1\",\"healthStatus\":\"healthy\",\"active\":true}}");

        var health = TestSupport.client(transport).accounts().health("a1", true);

        assertEquals("https://api.fopost.test/v1/accounts/a1/health?refresh=true", transport.last().url());
        assertEquals("healthy", health.healthStatus());
    }

    @Test
    void togglePrimaryReturnsTheNewState() {
        FakeTransport transport = new FakeTransport().enqueue(200, "{\"data\":{\"id\":\"a1\",\"isPrimary\":true}}");

        assertTrue(TestSupport.client(transport).accounts().togglePrimary("a1"));
    }

    @Test
    void labelsSendTheSnakeCaseWorkspaceKey() {
        FakeTransport transport = new FakeTransport().enqueue(201, "{\"id\":\"l1\",\"name\":\"Launch\"}");

        var label = TestSupport.client(transport).labels().create("w1", "Launch", "#4F46E5");

        assertEquals("l1", label.id());
        assertTrue(transport.lastBody().contains("\"workspace_id\":\"w1\""));
    }

    @Test
    void webhookCreateReturnsTheSigningSecretOnce() {
        FakeTransport transport = new FakeTransport()
                .enqueue(201, """
                        {"data":{"id":"h1","workspaceId":"w1","url":"https://example.test/hook",
                                 "events":["post.published"],"active":true,"secret":"whsec_123"}}""");

        Webhook webhook = TestSupport.client(transport)
                .webhooks()
                .create("w1", "https://example.test/hook", List.of(WebhookEvents.POST_PUBLISHED));

        assertEquals("whsec_123", webhook.secret());
        assertEquals(List.of("post.published"), webhook.events());
        assertTrue(transport.lastBody().contains("\"workspaceId\":\"w1\""));
    }

    @Test
    void automationToggleAndRunsPage() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"id\":\"m1\",\"active\":false}}")
                .enqueue(200, """
                        {"data":[{"id":7,"status":"completed","startedAt":"2026-08-01T00:00:00Z"}],
                         "meta":{"current_page":1,"per_page":30,"total":1,"last_page":1}}""");

        FoPost client = TestSupport.client(transport);

        assertFalse(client.automations().toggle("m1"));

        var runs = client.automations().runs("m1", 1, 30);
        assertEquals(1, runs.size());
        assertEquals(7L, runs.data().get(0).id());
    }

    @Test
    void mediaUploadSendsAMultipartBody() {
        FakeTransport transport = new FakeTransport()
                .enqueue(201, "{\"data\":[{\"id\":\"m1\",\"type\":\"image\",\"name\":\"a.png\","
                        + "\"url\":\"https://cdn.test/a.png\",\"size\":12}]}");

        var uploaded = TestSupport.client(transport)
                .media()
                .upload("w1", "a.png", new byte[] {1, 2, 3}, "image/png");

        assertEquals("m1", uploaded.id());
        assertEquals("image", uploaded.toMediaItem().type());
        String contentType = transport.last().headers().get("Content-Type");
        assertTrue(contentType.startsWith("multipart/form-data; boundary="));
        String body = transport.lastBody();
        assertTrue(body.contains("name=\"files\"; filename=\"a.png\""));
        assertTrue(body.contains("name=\"workspaceId\""));
    }

    @Test
    void analyticsOverviewUnwrapsAndEncodesItsFilters() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"totalAccounts\":2,\"totalFollowers\":1200,\"engagementRate\":3.5}}");

        var overview = TestSupport.client(transport)
                .analytics()
                .overview(AnalyticsParams.create().workspaceId("w1").days(30));

        assertEquals(2, overview.totalAccounts());
        assertEquals(1200L, overview.totalFollowers());
        assertEquals(3.5, overview.engagementRate());
        assertTrue(transport.last().url().contains("workspace_id=w1"));
        assertTrue(transport.last().url().contains("days=30"));
    }

    @Test
    void aiCaptionSendsSnakeCaseFieldsAndReadsTheCredits() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"caption\":\"Shipping today\",\"credits\":{\"charged\":2,\"remaining\":98}}");

        var result = TestSupport.client(transport)
                .ai()
                .generateCaption(CaptionParams.create()
                        .currentCaption("shipping a new feature")
                        .platforms("twitter", "linkedin"));

        assertEquals("Shipping today", result.caption());
        assertEquals(98, result.credits().remaining());
        assertTrue(transport.lastBody().contains("\"current_caption\":\"shipping a new feature\""));
    }
}
