package com.maan.eway.common.res;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCoverDetails implements Serializable{
	@JsonProperty("CoverId")
	public String coverId;
	@JsonProperty("CalcType")
	public String calcType;
	@JsonProperty("CoverName")
	public String coverName;
	@JsonProperty("CoverDesc")
	public String coverDesc;
	@JsonProperty("IsSubCover")
	public String isSubCover;
	@JsonProperty("SumInsured")
	public BigDecimal sumInsured;
	@JsonProperty("Rate")
	public Double rate;
	@JsonProperty("SubCoverId")
	public String subCoverId;
	@JsonProperty("SubCoverDesc")
	public String subCoverDesc;
	@JsonProperty("SubCoverName")
	public String subCoverName;
	@JsonProperty("Discounts")
	public List<SearchDiscount> discounts;
	@JsonProperty("Taxes")
	public List<SearchTax> taxes;

	@JsonProperty("SubCovers")
	public List<SearchCoverDetails> subcovers;

	@JsonProperty("Loadings")
	public List<SearchLoading> loadings;
	@JsonProperty("CoverageType")
	private String coverageType;
	@JsonProperty("isSelected")
	private String isselected;

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

	@JsonProperty("isReferal")
	private String isReferral;

	@JsonProperty("ReferalDescription")
	private String referalDescription;
	@JsonProperty("ProRata")
	private BigDecimal proRata;

	@JsonProperty("RegulatorRate")
	private Double tiraRate;

	@JsonProperty("UserOpt")
	private String userOpt;

	@JsonProperty("CoverBasedOn")
	private String coverBasedOn;

	@JsonProperty("VehicleId")
	private String vehicleId;

	 @JsonProperty("SectionName") 
	 private String sectionName;
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;
	@JsonProperty("Status")
	private String status;

	@JsonProperty("DiffPremiumIncludedTax")
	private BigDecimal diffPremiumIncludedTax;
	@JsonProperty("DiffPremiumIncludedTaxLC")
	private BigDecimal diffPremiumIncludedTaxLC;
	
	 @JsonProperty("ExcessPercent") 
	 private BigDecimal excessPercent;
	 @JsonProperty("ExcessAmount") 
	 private BigDecimal excessAmount;
	 @JsonProperty("ExcessDesc") 
	 private String excessDesc;


}


