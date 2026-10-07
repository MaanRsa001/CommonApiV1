package com.maan.eway.form.fields;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SectionVisibilityResponse
 *
 * Returned by POST /api/form-configs/section-visibility.
 *
 * Example:
 * {
 *   "sectionVisibilities": [
 *     { "sectionKey": "fire", "conditionField": "itemCount",    "currentValue": 3, "isVisible": true  },
 *     { "sectionKey": "par",  "conditionField": "parItemCount", "currentValue": 0, "isVisible": false }
 *   ]
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionVisibilityResponse {

    private List<SectionVisibilityItem> sectionVisibilities;

    // ── Inner item ────────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionVisibilityItem {

        /** Section key e.g. "fire", "par" */
        private String  sectionKey;

        /** The form-field key that was used as the count trigger */
        private String  conditionField;

        /** The count value passed in the request */
        private int     currentValue;

        /** True when the section's condition evaluates to visible */
        private boolean isVisible;
    }
}
