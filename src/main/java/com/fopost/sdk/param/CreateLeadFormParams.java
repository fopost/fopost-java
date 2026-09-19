package com.fopost.sdk.param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** An instant form on a Page. {@code questions} are {@code EMAIL}, {@code FULL_NAME} and {@code PHONE}. */
public final class CreateLeadFormParams {

    private final Map<String, Object> body = new LinkedHashMap<>();

    private CreateLeadFormParams() {}

    public static CreateLeadFormParams of(
            String workspaceId,
            String connectionId,
            String pageId,
            String name,
            List<String> questions,
            String privacyPolicyUrl,
            String thankYouMessage) {
        CreateLeadFormParams params = new CreateLeadFormParams();
        params.body.put("workspaceId", workspaceId);
        params.body.put("connectionId", connectionId);
        params.body.put("pageId", pageId);
        params.body.put("name", name);
        params.body.put("questions", List.copyOf(questions));
        params.body.put("privacyPolicyUrl", privacyPolicyUrl);
        params.body.put("thankYouMessage", thankYouMessage);
        return params;
    }

    /** Where the thank-you screen sends people. */
    public CreateLeadFormParams followUpUrl(String followUpUrl) {
        body.put("followUpUrl", followUpUrl);
        return this;
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(body);
    }
}
