package com.maan.eway.promoCode.res;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for one row from tbl_promo_code_header.
 * Every column is exposed so the grid can display full detail.
 */
@Getter
@Setter
@NoArgsConstructor
public class PromoCodeHeaderRes {

    @JsonProperty("PromoId")
    private Long promoId;

    @JsonProperty("PromoCode")
    private String promoCode;

    @JsonProperty("PromoDesc")
    private String promoDesc;

    @JsonProperty("CompanyId")
    private Integer companyId;

    @JsonProperty("BranchId")
    private Integer branchId;

    @JsonProperty("ProductId")
    private Integer productId;

    @JsonProperty("DiscountType")
    private String discountType;

    @JsonProperty("DiscountTypeDescription")
    private String discountTypeDescription;

    @JsonProperty("CalcType")
    private String calcType;

    @JsonProperty("CoreAppCode")
    private String coreAppCode;

    @JsonProperty("AmendId")
    private Integer amendId;

    @JsonProperty("EffectiveStartDate")
    private LocalDateTime effectiveStartDate;

    @JsonProperty("EffectiveEndDate")
    private LocalDateTime effectiveEndDate;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("UpdatedDate")
    private LocalDateTime updatedDate;

    @JsonProperty("CreatedBy")
    private String createdBy;

    @JsonProperty("EntryDate")
    private LocalDateTime entryDate;

    @JsonProperty("Remarks")
    private String remarks;
}
