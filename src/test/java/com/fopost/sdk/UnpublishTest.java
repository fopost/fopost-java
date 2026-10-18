package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UnpublishTest {

    @Test
    void unpublishTakesThePostDownOnEveryNetwork() {
        FakeTransport transport = new FakeTransport().enqueue(200, """
                {"post_status":"unpublished","deliveries":[
                  {"account_id":"a1","platform":"linkedin","removed":true},
                  {"account_id":"a2","platform":"threads","removed":false}]}""");

        var result = TestSupport.client(transport).posts().unpublish("p1");

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/posts/p1/unpublish", transport.last().url());
        assertEquals("unpublished", result.postStatus());
        assertTrue(result.deliveries().get(0).removed());
        assertFalse(result.deliveries().get(1).removed());
        assertEquals("threads", result.deliveries().get(1).platform());
    }
}
