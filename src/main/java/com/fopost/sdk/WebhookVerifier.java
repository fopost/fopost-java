package com.fopost.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fopost.sdk.internal.Json;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Verifies the signature on a webhook delivery and hands back the parsed envelope.
 *
 * <p>Every delivery carries {@code X-FoPost-Timestamp} and {@code X-FoPost-Signature-256}, an
 * HMAC-SHA256 over {@code <timestamp>.<rawBody>}; that pair is checked first and refused when the
 * timestamp is more than {@link #TOLERANCE} from now. The original {@code X-FoPost-Signature}, over
 * the body alone, is accepted only when no timestamp header is present.
 *
 * <p>Verify the exact bytes that arrived. Re-serialising parsed JSON changes key order, whitespace
 * and number formatting, and the signature no longer matches.
 *
 * <pre>{@code
 * byte[] rawBody = request.getInputStream().readAllBytes();
 * WebhookEnvelope envelope = WebhookVerifier.verify(rawBody, headers, secret);
 * }</pre>
 *
 * A failed check is a {@link FoPostException} with code {@code missing_signature},
 * {@code invalid_signature}, {@code stale_timestamp} or {@code invalid_payload}.
 */
public final class WebhookVerifier {

    public static final String SIGNATURE_HEADER = "X-FoPost-Signature";
    public static final String TIMESTAMPED_SIGNATURE_HEADER = "X-FoPost-Signature-256";
    public static final String TIMESTAMP_HEADER = "X-FoPost-Timestamp";
    public static final String EVENT_HEADER = "X-FoPost-Event";
    public static final String DELIVERY_HEADER = "X-FoPost-Delivery";

    /** How far a delivery's timestamp may sit from the current time. */
    public static final Duration TOLERANCE = Duration.ofSeconds(300);

    private static final String PREFIX = "sha256=";

    private WebhookVerifier() {}

    public static WebhookEnvelope verify(byte[] rawBody, Map<String, String> headers, String secret) {
        return verify(rawBody, headers, secret, Clock.systemUTC());
    }

    /** {@code rawBody} is the request body as received, before any parsing. */
    public static WebhookEnvelope verify(String rawBody, Map<String, String> headers, String secret) {
        return verify(rawBody.getBytes(StandardCharsets.UTF_8), headers, secret);
    }

    /** As {@link #verify(byte[], Map, String)}, reading the current time from {@code clock}. */
    public static WebhookEnvelope verify(byte[] rawBody, Map<String, String> headers, String secret, Clock clock) {
        if (rawBody == null || secret == null || secret.isEmpty()) {
            throw failure("missing_signature", "fopost: a body and the webhook secret are required");
        }
        String timestamp = header(headers, TIMESTAMP_HEADER);
        if (timestamp != null) {
            String signature = header(headers, TIMESTAMPED_SIGNATURE_HEADER);
            if (signature == null) {
                throw failure("missing_signature", "fopost: " + TIMESTAMPED_SIGNATURE_HEADER + " header is missing");
            }
            long sent;
            try {
                sent = Long.parseLong(timestamp.trim());
            } catch (NumberFormatException e) {
                throw failure("invalid_signature", "fopost: " + TIMESTAMP_HEADER + " is not a unix time");
            }
            long now = clock.instant().getEpochSecond();
            if (Math.abs(now - sent) > TOLERANCE.getSeconds()) {
                throw failure("stale_timestamp", "fopost: the delivery is outside the " + TOLERANCE.getSeconds()
                        + " second tolerance");
            }
            if (!matches(signature, sign(rawBody, secret, sent))) {
                throw failure("invalid_signature", "fopost: the timestamped signature does not match the body");
            }
        } else {
            String signature = header(headers, SIGNATURE_HEADER);
            if (signature == null) {
                throw failure("missing_signature", "fopost: " + SIGNATURE_HEADER + " header is missing");
            }
            if (!matches(signature, sign(rawBody, secret))) {
                throw failure("invalid_signature", "fopost: the signature does not match the body");
            }
        }
        return parse(rawBody);
    }

    /** The {@code X-FoPost-Signature} value the API sends for these bytes. */
    public static String sign(byte[] rawBody, String secret) {
        return PREFIX + hex(hmac(secret, rawBody));
    }

    /** The {@code X-FoPost-Signature-256} value the API sends for these bytes at {@code timestamp}. */
    public static String sign(byte[] rawBody, String secret, long timestamp) {
        byte[] prefix = (timestamp + ".").getBytes(StandardCharsets.UTF_8);
        byte[] signed = new byte[prefix.length + rawBody.length];
        System.arraycopy(prefix, 0, signed, 0, prefix.length);
        System.arraycopy(rawBody, 0, signed, prefix.length, rawBody.length);
        return PREFIX + hex(hmac(secret, signed));
    }

    private static boolean matches(String provided, String expected) {
        byte[] left = provided.trim().toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8);
        byte[] right = expected.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(left, right);
    }

    private static WebhookEnvelope parse(byte[] rawBody) {
        JsonNode body;
        try {
            body = Json.MAPPER.readTree(rawBody);
        } catch (IOException e) {
            throw failure("invalid_payload", "fopost: the webhook body is not JSON");
        }
        if (body == null || !body.isObject() || !body.path("event").isTextual()) {
            throw failure("invalid_payload", "fopost: the webhook body has no event");
        }
        return new WebhookEnvelope(
                body.get("event").asText(), body.path("data"), body.path("timestamp").asText(null));
    }

    private static byte[] hmac(String secret, byte[] message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(message);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new FoPostException("fopost: HmacSHA256 is not available", e);
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder out = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            out.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
        }
        return out.toString();
    }

    private static String header(Map<String, String> headers, String name) {
        if (headers == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(name) && entry.getValue() != null) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static FoPostException failure(String code, String message) {
        return new FoPostException(message, 0, code, null);
    }
}
