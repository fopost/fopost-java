package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.AdBusinessCenter;
import com.fopost.sdk.model.AdCommentsPage;
import com.fopost.sdk.model.AdIdentity;
import com.fopost.sdk.model.SparkPost;
import com.fopost.sdk.param.AdBudgetParams;
import com.fopost.sdk.param.AdTargetingParams;
import com.fopost.sdk.param.CreateAdCampaignParams;
import com.fopost.sdk.param.CreateAdParams;
import com.fopost.sdk.param.UploadConversionsParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdsTikTokTest {

    @Test
    void identitiesAndSparkPostsReadTheRightPaths() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":[{\"id\":\"bc1\",\"name\":\"Brand HQ\",\"role\":\"ADMIN\"}]}")
                .enqueue(200, "{\"data\":[{\"id\":\"idt_1\",\"type\":\"CUSTOMIZED_USER\",\"name\":\"Your Brand\","
                        + "\"avatarUrl\":null}]}")
                .enqueue(200, "{\"data\":[{\"id\":\"item_99\",\"identityId\":\"idt_1\",\"caption\":null,"
                        + "\"thumbnailUrl\":null,\"createdAt\":null,\"views\":48213}]}");

        FoPost client = TestSupport.client(transport);

        List<AdBusinessCenter> centers = client.ads().tiktokBusinessCenters("c1", "w1");
        assertEquals("Brand HQ", centers.get(0).name());
        assertTrue(transport.last().url().contains("/v1/ads/tiktok/business-centers"));

        List<AdIdentity> identities = client.ads().tiktokIdentities("c1", "7011", "w1");
        assertEquals("CUSTOMIZED_USER", identities.get(0).type());

        List<SparkPost> posts = client.ads().sparkPosts("c1", "7011", "idt_1", "w1");
        assertEquals(48213L, posts.get(0).views());
        assertTrue(transport.last().url().contains("identity_id=idt_1"));
    }

    @Test
    void sparkPostIdAndSmartPlusTravelInTheBody() {
        FakeTransport transport = new FakeTransport()
                .enqueue(201, "{\"data\":{\"id\":\"ad1\",\"workspaceId\":\"w1\",\"kind\":\"ad\",\"name\":\"Spark\","
                        + "\"goal\":\"traffic\",\"status\":\"paused\"}}")
                .enqueue(201, "{\"data\":{\"id\":\"c1\",\"name\":\"Smart\",\"status\":\"PAUSED\"}}");

        FoPost client = TestSupport.client(transport);

        client.ads()
                .create(CreateAdParams.of(
                                "w1",
                                "c1",
                                "7011",
                                "idt_1",
                                "Spark",
                                "traffic",
                                AdBudgetParams.daily(2000),
                                AdTargetingParams.create(List.of("US"), 18, 44, "all"),
                                "")
                        .sparkPostId("item_99"));
        assertTrue(transport.lastBody().contains("\"sparkPostId\":\"item_99\""));

        client.ads()
                .createCampaign(CreateAdCampaignParams.of("w1", "c1", "7011", "Smart", "traffic")
                        .smartPlus(true));
        assertTrue(transport.lastBody().contains("\"smartPlus\":true"));
    }

    @Test
    void conversionsReportWhatTheNetworkAccepted() {
        FakeTransport transport = new FakeTransport().enqueue(202, "{\"data\":{\"accepted\":2}}");

        long accepted = TestSupport.client(transport)
                .ads()
                .uploadConversions(UploadConversionsParams.of("w1", "c1", "7011", "px_1")
                        .event("CompletePayment", "2026-09-18T10:04:00Z")
                        .with("valueMinor", 4999)
                        .event("CompletePayment", "2026-09-18T11:04:00Z"));

        assertEquals(2L, accepted);
        assertTrue(transport.lastBody().contains("\"pixelId\":\"px_1\""));
        assertTrue(transport.lastBody().contains("\"valueMinor\":4999"));
    }

    @Test
    void commentsPageAndTheThreeWrites() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, "{\"data\":{\"comments\":[{\"id\":\"cm1\",\"adId\":\"ad1\",\"text\":\"nice\","
                        + "\"authorName\":null,\"authorAvatarUrl\":null,\"createdAt\":null,\"likes\":3,"
                        + "\"replyCount\":0,\"hidden\":true,\"parentId\":null}],\"nextCursor\":\"2\"}}")
                .enqueue(201, "{\"data\":{\"replyId\":\"cm2\"}}")
                .enqueue(200, "{\"message\":\"Comment hidden\"}")
                .enqueue(200, "{\"message\":\"Comment deleted\"}");

        FoPost client = TestSupport.client(transport);

        AdCommentsPage page = client.ads().comments("c1", "ad1", null, "w1");
        assertEquals("2", page.nextCursor());
        assertTrue(page.comments().get(0).hidden());
        assertEquals(3L, page.comments().get(0).likes());

        assertEquals("cm2", client.ads().replyToComment("cm1", "w1", "c1", "ad1", "Friday!"));
        assertTrue(transport.last().url().endsWith("/v1/ads/comments/cm1/reply"));
        assertTrue(transport.lastBody().contains("\"adId\":\"ad1\""));

        client.ads().setCommentHidden("cm1", "w1", "c1", "ad1", true);
        assertTrue(transport.lastBody().contains("\"hidden\":true"));

        client.ads().deleteComment("cm1", "w1", "c1", "ad1");
        // The ad travels in the body, because the path already carries the comment.
        assertEquals("DELETE", transport.last().method());
        assertTrue(transport.lastBody().contains("\"adId\":\"ad1\""));
    }
}
