package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.SubCoverRes;

import lombok.Data;

@Data
public class SearchPremiumCoverDetailsRes {

	@JsonProperty("VehicleId")
	private String vehicleId;
	@JsonProperty("CoverId")
	public String coverId;
	@JsonProperty("Rate")
	public Double rate;

	@JsonProperty("CoverName")
	public String coverName;
	@JsonProperty("CoverDesc")
	public String coverDesc;
	@JsonProperty("IsSubCover")
	public String isSubCover;
	@JsonProperty("SumInsured")
	public BigDecimal sumInsured;

	@JsonProperty("SubCovers")
	public List<SubCoverRes> subcovers;

	@JsonProperty("CoverageType")
	private String coverageType;

	@JsonProperty("PremiumExcluedTaxLC")
	private BigDecimal premiumExcluedTaxLC;
	@JsonProperty("PremiumIncludedTaxLC")
	private BigDecimal premiumIncludedTaxLC;

	@JsonProperty("PremiumExcluedTax")
	private BigDecimal premiumExcluedTax;
	@JsonProperty("PremiumIncludedTax")
	private BigDecimal premiumIncludedTax;

	@JsonProperty("PremiumBeforeDiscount")
	private BigDecimal premiumBeforeDiscount;
	@JsonProperty("PremiumAfterDiscount")
	private BigDecimal premiumAfterDiscount;
	@JsonProperty("PremiumBeforeDiscountLC")
	private BigDecimal premiumBeforeDiscountLC;
	@JsonProperty("PremiumAfterDiscountLC")
	private BigDecimal premiumAfterDiscountLC;

	@JsonProperty("ExcessPercent")
	private String excessPercent;
	@JsonProperty("ExcessAmount")
	private String excessAmount;
	@JsonProperty("ExcessDesc")
	private String excessDesc;
	@JsonProperty("Currency")
	private String Currency;
	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("SectionName")
	private String sectionName;

	@JsonProperty("TaxId")
	private Integer taxId;
	
	@JsonProperty("TaxRate")
	private BigDecimal taxRate;

	@JsonProperty("TaxAmount")
	private BigDecimal taxAmount;

	@JsonProperty("TaxDesc")
	private String taxDesc;

	@JsonProperty("TaxCalcType")
	private String taxCalcType;

	@JsonProperty("IsTaxExtempted")
	private String isTaxExtempted;

	@JsonProperty("TaxExemptType")
	private String taxExemptType;

	@JsonProperty("TaxExemptCode")
	private String taxExemptCode;
	
	
	@JsonProperty("CoverageLimit")
	private String coverageLimit;
	

}