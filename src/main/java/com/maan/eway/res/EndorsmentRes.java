package com.maan.eway.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndorsmentRes {

	@JsonProperty("CoverId")
	public String coverId;

	@JsonProperty("CoverName")
	public String coverName;

	@JsonProperty("SumInsured")
	public BigDecimal sumInsured;

	@JsonProperty("SumInsuredLc")
	public BigDecimal sumInsuredLc;

	@JsonProperty("CoverageType")
	private String coverageType;

	@JsonProperty("CalcType")
	private String calcType;

	@JsonProperty("PremiumBeforeDiscountLC")
	private BigDecimal premiumBeforeDiscountLC;

	@JsonProperty("PremiumAfterDiscountLC")
	private BigDecimal premiumAfterDiscountLC;

	@JsonProperty("PremiumExcluedTaxLC")
	private BigDecimal premiumExcluedTaxLC;

	@JsonProperty("PremiumIncludedTaxLC")
	private BigDecimal premiumIncludedTaxLC;

	@JsonProperty("PremiumBeforeDiscount")
	private BigDecimal premiumBeforeDiscount;

	@JsonProperty("PremiumAfterDiscount")
	private BigDecimal premiumAfterDiscount;

	@JsonProperty("PremiumExcluedTax")
	private BigDecimal premiumExcluedTax;

	@JsonProperty("PremiumIncludedTax")
	private BigDecimal premiumIncludedTax;

	@JsonProperty("ExcessAmount")
	private String excessAmount;

	@JsonProperty("ExcessPercent")
	private String excessPercent;

	@JsonProperty("MinimumPremiumYn")
	private String minimumPremiumYn;

	@JsonProperty("CoverageLimit")
	private String coverageLimit;

	@JsonProperty("PolicyPeriod")
	private String policyPeriod;

}
