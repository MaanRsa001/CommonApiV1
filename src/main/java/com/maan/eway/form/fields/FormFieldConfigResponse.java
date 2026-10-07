package com.maan.eway.form.fields;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.maan.eway.form.fields.FormFieldConfig.WrapperType;


/**
 * FormFieldConfigResponse
 *
 * Outgoing response payload returned by all form field config APIs.
 * Carries the complete field configuration including API config for dropdowns.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormFieldConfigResponse {

    // ── Identity ──────────────────────────────────────────────────────────────
    private Long          id;
    private String          fieldKey;
    private String          fieldType;

    // ── Display ───────────────────────────────────────────────────────────────
    private String          label;
    private String          placeholder;
    private String          description;
    private String          className;
    private String          defaultValue;
    private String          inputType;
    private Integer         textareaRows;

    // ── Validation ────────────────────────────────────────────────────────────
    private Boolean         isRequired;
    private Integer         minLength;
    private Integer         maxLength;
    private BigDecimal      minValue;
    private BigDecimal      maxValue;
    private String          pattern;
    private List<String>    validators;
    private Map<String, Object>          validatorParams;

    // ── Behaviour ─────────────────────────────────────────────────────────────
    private Boolean         isHidden;
    private Boolean         isDisabled;

    // ── Dropdown API Config ────────────────────────────────────────────────────
    private String          apiUrl;
    private List<String>    requestKeys;
    private String          apiMethod;
    private String          apiValueKey;
    private String          apiLabelKey;
    private String          apiDataPath;
    private String          apiDependsOn;
    private String          apiDependsOnParam;
    private List<Map<String, Object>>     apiHeaders;
    private List<Map<String, Object>>    apiQueryParams;

    // ── Section / Ordering ─────────────────────────────────────────────────────
    private String          sectionKey;
    private String          sectionTitle;
    private String          sectionSubtitle;
    private String          sectionIcon;
    private String          sectionGradient;
    private WrapperType     wrapperType;
    private Integer         sortOrder;

    // ── Count-Based Section Display ────────────────────────────────────────────
    private String          countConditionField;
    private String          countConditionOp;
    private Integer         countConditionValue;
    
    private Integer         companyId;
    private Integer         sectionId;
    private Integer         productId;

    // ── Audit ──────────────────────────────────────────────────────────────────
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;
}
