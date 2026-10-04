package com.fopost.sdk.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Create a conversion action, or send conversions Google did not see itself. */
public final class GoogleConversionParams {

    private GoogleConversionParams() {}

    /** Adds a conversion action. */
    public static final class CreateAction {

        private final Map<String, Object> body;

        public CreateAction(GoogleAdsScope scope, String name, String category) {
            body = scope.toMap();
            body.put("name", name);
            body.put("category", category);
        }

        /** The account's currency, in minor units. */
        public CreateAction valueMinor(long minor) {
            body.put("valueMinor", minor);
            return this;
        }

        /** {@code ONE_PER_CLICK} or {@code MANY_PER_CLICK}. */
        public CreateAction countingType(String countingType) {
            body.put("countingType", countingType);
            return this;
        }

        public Map<String, Object> toMap() {
            return body;
        }
    }

    /** Sends offline conversions, each matched to a click. */
    public static final class Upload {

        private final Map<String, Object> body;
        private final List<Map<String, Object>> conversions = new ArrayList<>();

        public Upload(GoogleAdsScope scope) {
            body = scope.toMap();
        }

        /**
         * One conversion. {@code clickIdField} is gclid, gbraid or wbraid: one of them is required,
         * because it is what matches the click. {@code conversionDateTime} is
         * {@code yyyy-MM-dd HH:mm:ss+|-HH:mm}, the only shape Google accepts.
         */
        public Upload conversion(
                String clickIdField,
                String clickId,
                String conversionActionId,
                String conversionDateTime) {
            Map<String, Object> conversion = new LinkedHashMap<>();
            conversion.put(clickIdField, clickId);
            conversion.put("conversionActionId", conversionActionId);
            conversion.put("conversionDateTime", conversionDateTime);
            conversions.add(conversion);
            return this;
        }

        /** Puts a value on the conversion added last. */
        public Upload valueMinor(long minor, String currencyCode) {
            Map<String, Object> last = conversions.get(conversions.size() - 1);
            last.put("valueMinor", minor);
            last.put("currencyCode", currencyCode);
            return this;
        }

        /** Puts an order id on the conversion added last. */
        public Upload orderId(String orderId) {
            conversions.get(conversions.size() - 1).put("orderId", orderId);
            return this;
        }

        public Map<String, Object> toMap() {
            body.put("conversions", conversions);
            return body;
        }
    }

    /** Restates, retracts or enhances conversions already counted. */
    public static final class Adjust {

        private final Map<String, Object> body;
        private final List<Map<String, Object>> adjustments = new ArrayList<>();

        public Adjust(GoogleAdsScope scope) {
            body = scope.toMap();
        }

        /** {@code adjustmentType} is RESTATEMENT, RETRACTION or ENHANCEMENT. */
        public Adjust adjustment(
                String conversionActionId, String adjustmentType, String adjustmentDateTime) {
            Map<String, Object> adjustment = new LinkedHashMap<>();
            adjustment.put("conversionActionId", conversionActionId);
            adjustment.put("adjustmentType", adjustmentType);
            adjustment.put("adjustmentDateTime", adjustmentDateTime);
            adjustments.add(adjustment);
            return this;
        }

        /** Identifies the conversion the adjustment added last applies to. */
        public Adjust matching(String gclid, String orderId, String conversionDateTime) {
            Map<String, Object> last = adjustments.get(adjustments.size() - 1);
            if (gclid != null) {
                last.put("gclid", gclid);
            }
            if (orderId != null) {
                last.put("orderId", orderId);
            }
            if (conversionDateTime != null) {
                last.put("conversionDateTime", conversionDateTime);
            }
            return this;
        }

        /** The restated value of the adjustment added last. */
        public Adjust restatementValueMinor(long minor, String currencyCode) {
            Map<String, Object> last = adjustments.get(adjustments.size() - 1);
            last.put("restatementValueMinor", minor);
            last.put("currencyCode", currencyCode);
            return this;
        }

        public Map<String, Object> toMap() {
            body.put("adjustments", adjustments);
            return body;
        }
    }
}
