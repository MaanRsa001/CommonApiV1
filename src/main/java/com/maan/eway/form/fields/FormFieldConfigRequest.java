package com.maan.eway.form.fields;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.maan.eway.form.fields.FormFieldConfig.WrapperType;


/**
 * FormFieldConfigRequest
 *
 * Incoming request payload for creating or updating a form field configuration.
 * Used by POST and PUT endpoints in the controller.
 * Validation annotations applied to enforce required fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormFieldConfigRequest {

    // ── Identity ──────────────────────────────────────────────────────────────

    /** Required for update; omit for create (auto-generated or client-assigned). */
    private Long          id;

    @NotBlank(message = "Field key must not be blank")
    private String          fieldKey;

    @NotBlank(message = "Field type must not be blank")
    private String          fieldType;

    // ── Display ───────────────────────────────────────────────────────────────

    @NotBlank(message = "Label must not be blank")
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
    // Required when fieldType is primeng-select, p-select, or radio

    private String          apiUrl;
    private List<String>    requestKeys;
    private String          apiMethod;
    private String          apiValueKey;
    private String          apiLabelKey;
    private String          apiDataPath;
    private String          apiDependsOn;
    private String          apiDependsOnParam;
    private List<Map<String, Object>>    apiHeaders;
    private List<Map<String, Object>>    apiQueryParams;

    // ── Section / Ordering ─────────────────────────────────────────────────────

    @NotBlank(message = "Section key must not be blank")
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
}
