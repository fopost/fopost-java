package com.fopost.sdk.model;

import java.time.Instant;
import java.util.List;

/**
 * A product on a connected store. {@code price} is the lowest variant price, as
 * a decimal string; {@code status} is {@code active}, {@code draft} or
 * {@code archived}.
 */
public record RemoteProduct(
        String id,
        String title,
        String handle,
        String status,
        String description,
        String vendor,
        String productType,
        List<String> tags,
        String imageUrl,
        String url,
        String price,
        String currency,
        Instant updatedAt) {}
