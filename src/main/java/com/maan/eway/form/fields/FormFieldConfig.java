package com.maan.eway.form.fields;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "form_field_configs")
public class FormFieldConfig {

    // ── Identity ──────────────────────────────────────────────────────────────

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "FIELD_KEY", length = 100, nullable = false)
    private String fieldKey;

    @Column(name = "FIELD_TYPE", length = 50, nullable = false)
    private String fieldType;

    // ── Display ───────────────────────────────────────────────────────────────

    @Column(name = "LABEL", length = 200, nullable = false)
    private String label;

    @Column(name = "PLACEHOLDER", length = 200)
    private String placeholder;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "CLASS_NAME", length = 50)
    private String className;

    @Column(name = "DEFAULT_VALUE", length = 255)
    private String defaultValue;

    @Column(name = "INPUT_TYPE", length = 50)
    private String inputType;

    @Column(name = "TEXTAREA_ROWS")
    private Integer textareaRows;

    // ── Validation ────────────────────────────────────────────────────────────

    @Column(name = "IS_REQUIRED")
    private Boolean isRequired;

    @Column(name = "MIN_LENGTH")
    private Integer minLength;

    @Column(name = "MAX_LENGTH")
    private Integer maxLength;

    @Column(name = "MIN_VALUE", precision = 15, scale = 4)
    private BigDecimal minValue;

    @Column(name = "MAX_VALUE", precision = 15, scale = 4)
    private BigDecimal maxValue;

    @Column(name = "PATTERN", length = 500)
    private String pattern;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "VALIDATORS_JSON")
    private List<String> validators;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "VALIDATOR_PARAMS_JSON")
    private Map<String, Object> validatorParams;

    // ── Behaviour ─────────────────────────────────────────────────────────────

    @Column(name = "IS_HIDDEN")
    private Boolean isHidden;

    @Column(name = "IS_DISABLED")
    private Boolean isDisabled;

    // ── Dropdown API Config ────────────────────────────────────────────────────

    @Column(name = "API_URL", length = 1000)
    private String apiUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "REQUEST_KEYS")
    private List<String> requestKeys;

    @Column(name = "API_METHOD", length = 10)
    private String apiMethod;

    @Column(name = "API_VALUE_KEY", length = 100)
    private String apiValueKey;

    @Column(name = "API_LABEL_KEY", length = 100)
    private String apiLabelKey;

    @Column(name = "API_DATA_PATH", length = 200)
    private String apiDataPath;

    @Column(name = "API_DEPENDS_ON", length = 100)
    private String apiDependsOn;

    @Column(name = "API_DEPENDS_ON_PARAM", length = 100)
    private String apiDependsOnParam;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "API_HEADERS_JSON")
    private List<Map<String, Object>> apiHeaders;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "API_QUERY_PARAMS_JSON")
    private List<Map<String, Object>> apiQueryParams;

    // ── Section / Ordering ─────────────────────────────────────────────────────

    @Column(name = "SECTION_KEY", length = 50, nullable = false)
    private String sectionKey;

    @Column(name = "SECTION_TITLE", length = 100)
    private String sectionTitle;

    @Column(name = "SECTION_SUBTITLE", length = 200)
    private String sectionSubtitle;

    @Column(name = "SECTION_ICON", length = 50)
    private String sectionIcon;

    @Column(name = "SECTION_GRADIENT", length = 200)
    private String sectionGradient;

    @Enumerated(EnumType.STRING)
    @Column(name = "WRAPPER_TYPE", length = 10)
    private WrapperType wrapperType;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder;

    // ── Count-Based Section Display ────────────────────────────────────────────

    @Column(name = "COUNT_CONDITION_FIELD", length = 100)
    private String countConditionField;

    @Column(name = "COUNT_CONDITION_OP", length = 10)
    private String countConditionOp;

    @Column(name = "COUNT_CONDITION_VALUE")
    private Integer countConditionValue;
    
    @Column(name = "COMPANY_ID")
    private Integer companyId;
    
    @Column(name = "SECTION_ID")
    private Integer sectionId;
    
    @Column(name = "PRODUCT_ID")
    private Integer productId;

    // ── Audit ──────────────────────────────────────────────────────────────────

    @Column(name = "IS_ACTIVE")
    private Boolean isActive;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.isActive  = (this.isActive  == null) ? Boolean.TRUE  : this.isActive;
        this.isRequired= (this.isRequired== null) ? Boolean.FALSE : this.isRequired;
        this.isHidden  = (this.isHidden  == null) ? Boolean.FALSE : this.isHidden;
        this.isDisabled= (this.isDisabled== null) ? Boolean.FALSE : this.isDisabled;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum WrapperType {
        grid, repeat
    }
}


