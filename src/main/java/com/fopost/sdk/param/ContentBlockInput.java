package com.fopost.sdk.param;

import com.fopost.sdk.model.MediaItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One block of a post being written.
 *
 * <pre>{@code
 * ContentBlockInput.text("Second one, with an image")
 *     .media(MediaItem.of("image", "chart.png", "https://.../chart.png"));
 * }</pre>
 */
public final class ContentBlockInput {

    private final String text;
    private final List<MediaItem> media = new ArrayList<>();

    private ContentBlockInput(String text) {
        this.text = text;
    }

    public static ContentBlockInput text(String text) {
        return new ContentBlockInput(text);
    }

    public ContentBlockInput media(MediaItem... items) {
        for (MediaItem item : items) {
            if (item != null) {
                media.add(item);
            }
        }
        return this;
    }

    public ContentBlockInput media(List<MediaItem> items) {
        if (items != null) {
            media.addAll(items);
        }
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> block = new LinkedHashMap<>();
        block.put("text", text);
        if (!media.isEmpty()) {
            List<Map<String, Object>> encoded = new ArrayList<>();
            for (MediaItem item : media) {
                Map<String, Object> entry = new LinkedHashMap<>();
                Params.put(entry, "type", item.type());
                Params.put(entry, "name", item.name());
                Params.put(entry, "url", item.url());
                Params.put(entry, "size", item.size());
                Params.put(entry, "alt", item.alt());
                Params.put(entry, "thumbnail", item.thumbnail());
                encoded.add(entry);
            }
            block.put("media", encoded);
        }
        return block;
    }
}
