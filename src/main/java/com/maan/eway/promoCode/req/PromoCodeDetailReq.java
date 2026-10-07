package com.maan.eway.promoCode.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromoCodeDetailReq {

    @JsonProperty("products")
    private String products;

    @JsonProperty("sectionIds")
    private Integer sectionIds;

    /**
     * Per-line DISC. TYPE (visible as dropdown in the UI grid).
     * Allowed values: "P" = Percentage, "A" = Amount
     * This replaces the old single header-level calcType for discount calculation.
     */
    @JsonProperty("calcType")
    private String calcType;

    @JsonProperty("minPremium")
    private Double minPremium;

    @JsonProperty("maxPremium")
    private Double maxPremium;

    @JsonProperty("minDiscount")
    private Double minDiscount;

    @JsonProperty("maxDiscount")
    private Double maxDiscount;

    @JsonProperty("agencyCodes")
    private List<String> agencyCodes;
    
    @JsonProperty("channelType")
    private String channelType;

    @JsonProperty("typeOfBusiness")
    private String typeOfBusiness;
}