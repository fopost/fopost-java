package com.fopost.sdk.param;

import java.util.List;
import java.util.Map;

/** Create or change a Performance Max asset group. */
public final class GoogleAssetGroupParams {

    private GoogleAssetGroupParams() {}

    /** Creates one. It starts paused unless a status says otherwise. */
    public static final class Create {

        private final Map<String, Object> body;

        public Create(
                GoogleAdsScope scope, String campaignId, String name, List<String> finalUrls) {
            body = scope.toMap();
            body.put("campaignId", campaignId);
            body.put("name", name);
            body.put("finalUrls", finalUrls);
        }

        /** {@code active} or {@code paused}. */
        public Create status(String status) {
            body.put("status", status);
            return this;
        }

        public Map<String, Object> toMap() {
            return body;
        }
    }

    /** Renames, pauses or resumes one. */
    public static final class Update {

        private final Map<String, Object> body;

        public Update(GoogleAdsScope scope) {
            body = scope.toMap();
        }

        public Update name(String name) {
            body.put("name", name);
            return this;
        }

        public Update status(String status) {
            body.put("status", status);
            return this;
        }

        public Map<String, Object> toMap() {
            return body;
        }
    }
}
