package com.maan.eway.promoCode.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for one row from promo_code_mapping.
 */
@Getter
@Setter
@NoArgsConstructor
public class PromoCodeMappingRes {

    @JsonProperty("MappingId")
    private Long mappingId;

    @JsonProperty("PromoId")
    private Long promoId;

    @JsonProperty("PromoCode")
    private String promoCode;

    @JsonProperty("ProductId")
    private Integer productId;

    @JsonProperty("SectionId")
    private Integer sectionId;

    @JsonProperty("TypeOfBusiness")
    private String typeOfBusiness;

    @JsonProperty("BusinessDescription")
    private String businessDescription;

    @JsonProperty("CalcType")
    private String calcType;

    @JsonProperty("MinPremium")
    private BigDecimal minPremium;

    @JsonProperty("MaxPremium")
    private BigDecimal maxPremium;

    @JsonProperty("MinDiscount")
    private BigDecimal minDiscount;

    @JsonProperty("MaxDiscount")
    private BigDecimal maxDiscount;

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
}