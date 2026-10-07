package com.maan.eway.form.fields;

import java.util.Map;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SectionVisibilityRequest
 *
 * Request payload for the count-based section visibility API.
 *
 * Example:
 * {
 *   "countValues": {
 *     "itemCount":    3,
 *     "parItemCount": 0
 *   }
 * }
 *
 * Each key is a form-field key configured as countConditionField.
 * Each value is the current live count from the form.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionVisibilityRequest {

    @NotEmpty(message = "countValues map must not be empty")
    private Map<String, Integer> countValues;
}
