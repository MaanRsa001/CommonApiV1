package com.maan.eway.req;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UnderwriterAdjustmentReq {

    @JsonProperty("CoverId")
    private String coverId;

    // --- Underwriter Discount ---
    @JsonProperty("UwDiscountYn")
    private String uwDiscountYn;       // Y or N

    @JsonProperty("UwDiscountType")
    private String uwDiscountType;     // P = Percentage, A = Amount

    @JsonProperty("UwDiscountValue")
    private BigDecimal uwDiscountValue;

    @JsonProperty("UwDiscountDesc")
    private String uwDiscountDesc;     // Reason or remark

    // --- Underwriter Loading ---
    @JsonProperty("UwLoadingYn")
    private String uwLoadingYn;        // Y or N

    @JsonProperty("UwLoadingType")
    private String uwLoadingType;      // P = Percentage, A = Amount

    @JsonProperty("UwLoadingValue")
    private BigDecimal uwLoadingValue;

    @JsonProperty("UwLoadingDesc")
    private String uwLoadingDesc;
}
