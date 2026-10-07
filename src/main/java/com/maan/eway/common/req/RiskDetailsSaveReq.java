package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RiskDetailsSaveReq {

	
	@JsonProperty("RiskCode")
    private String     riskCode     ;
	@JsonProperty("SumInsured")
    private String      sumInsured   ;
	@JsonProperty("SumInsuredEquivalent")
    private String      sumInsuredEquivalent ;
	@JsonProperty("PremiumRate")
    private String      premiumRate  ;
	@JsonProperty("PremiumBeforeDiscount")
    private String      premiumBeforeDiscount ;
	@JsonProperty("PremiumAfterDiscount")
    private String      premiumAfterDiscount ;
	@JsonProperty("PremiumExcludingTaxEquivalent")
    private String      premiumExcludingTaxEquivalent ;
	@JsonProperty("PremiumExcludingTax")
    private String      premiumExcludingTax ;
	@JsonProperty("PremiumIncludingTax")
    private String      premiumIncludingTax ;
	@JsonProperty("DiscountType")
    private String     discountType ;
	@JsonProperty("DiscountRate")
    private String      discountRate ;
	@JsonProperty("DiscountAmount")
    private String      discountAmount ;
	@JsonProperty("Taxcode")
    private String     taxCode      ;
	@JsonProperty("IsTaxExemptedId")
    private String     isTaxExemptedId ;
	@JsonProperty("TaxExcemptionTypeId")
    private String     taxExcemptionTypeId ;
	@JsonProperty("TaxExemptionReference")
    private String     taxExemptionReference ;
	@JsonProperty("TaxRate")
    private String      taxRate      ;
	@JsonProperty("TaxAmount")
    private String      taxAmount    ;
	
}
