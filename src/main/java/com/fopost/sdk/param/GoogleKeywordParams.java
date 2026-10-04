package com.fopost.sdk.param;

import java.util.Map;

/** Add a keyword to an ad group, or change one that is already there. */
public final class GoogleKeywordParams {

    private GoogleKeywordParams() {}

    /** Adds a keyword. {@code matchType} is EXACT, PHRASE or BROAD. */
    public static final class Create {

        private final Map<String, Object> body;

        public Create(GoogleAdsScope scope, String adGroupId, String text, String matchType) {
            body = scope.toMap();
            body.put("adGroupId", adGroupId);
            body.put("text", text);
            body.put("matchType", matchType);
        }

        /** The account's currency, in minor units. */
        public Create cpcBidMinor(long minor) {
            body.put("cpcBidMinor", minor);
            return this;
        }

        public Map<String, Object> toMap() {
            return body;
        }
    }

    /** Pauses, resumes or rebids a keyword. */
    public static final class Update {

        private final Map<String, Object> body;

        public Update(GoogleAdsScope scope) {
            body = scope.toMap();
        }

        /** {@code active} or {@code paused}. */
        public Update status(String status) {
            body.put("status", status);
            return this;
        }

        public Update cpcBidMinor(long minor) {
            body.put("cpcBidMinor", minor);
            return this;
        }

        public Map<String, Object> toMap() {
            return body;
        }
    }
}
