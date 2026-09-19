package com.fopost.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fopost.sdk.model.LengthValidation;
import com.fopost.sdk.model.MediaValidation;
import com.fopost.sdk.model.PostValidation;
import com.fopost.sdk.param.ValidatePostParams;
import java.util.List;
import org.junit.jupiter.api.Test;

class ValidateTest {

    @Test
    void postSendsTheDraftAndReadsThePerPlatformChecks() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"ready":false,"platforms":[
                          {"platform":"twitter","ready":false,"issues":["Text exceeds 280 characters"],
                           "score":42,"signals":[{"level":"warn","code":"over_length","message":"Too long"}]},
                          {"platform":"linkedin","ready":true,"issues":[],"signals":[]}]}}""");

        PostValidation result = TestSupport.client(transport)
                .validate()
                .post(ValidatePostParams.of("twitter", "linkedin")
                        .content("Shipping today")
                        .media("https://cdn.example.test/chart.png", "image/png", 1234L));

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/validate/post", transport.last().url());
        assertEquals(
                "{\"content\":\"Shipping today\","
                        + "\"media\":[{\"url\":\"https://cdn.example.test/chart.png\",\"mime_type\":\"image/png\",\"size\":1234}],"
                        + "\"platforms\":[\"twitter\",\"linkedin\"]}",
                transport.lastBody());
        assertFalse(result.isReady());
        assertEquals(2, result.platforms().size());
        assertEquals("twitter", result.platforms().get(0).platform());
        assertEquals(42.0, result.platforms().get(0).score());
        assertEquals("over_length", result.platforms().get(0).signals().get(0).code());
        assertNull(result.platforms().get(1).score());
    }

    @Test
    void lengthSendsTextAndPlatformsAndReadsTheLimits() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"ok":true,"platforms":[
                          {"platform":"twitter","length":14,"limit":280,"unit":"chars","ok":true,"signals":[]},
                          {"platform":"linkedin","length":14,"limit":null,"unit":"chars","ok":true,"signals":[]}]}}""");

        LengthValidation result = TestSupport.client(transport).validate().length("Shipping today", "twitter", "linkedin");

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/validate/length", transport.last().url());
        assertEquals("{\"text\":\"Shipping today\",\"platforms\":[\"twitter\",\"linkedin\"]}", transport.lastBody());
        assertTrue(result.isOk());
        assertEquals(280, result.platforms().get(0).limit());
        assertNull(result.platforms().get(1).limit());
        assertEquals("chars", result.platforms().get(1).unit());
    }

    @Test
    void mediaSendsTheUrlAndReadsTheCheck() {
        FakeTransport transport = new FakeTransport()
                .enqueue(200, """
                        {"data":{"ok":false,"issues":["File is larger than 50 MB"],"name":"big.mov","size":60000000}}""");

        MediaValidation result = TestSupport.client(transport).validate().media("https://cdn.example.test/big.mov");

        assertEquals("POST", transport.last().method());
        assertEquals("https://api.fopost.test/v1/validate/media", transport.last().url());
        assertEquals("{\"url\":\"https://cdn.example.test/big.mov\"}", transport.lastBody());
        assertFalse(result.isOk());
        assertEquals(List.of("File is larger than 50 MB"), result.issues());
        assertEquals("big.mov", result.name());
        assertEquals(60000000L, result.size());
        assertNull(result.mimeType());
        assertNull(result.type());
    }
}
