package com.maan.eway.res.calc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Endorsement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("EndorsementId")
	private String endorsementId;
	@JsonProperty("EndorsementDesc")
	private String endorsementDesc;
	@JsonProperty("EndorsementRate")
	private Double endorsementRate;

	// @JsonProperty("EndorsementAmount")
	// private BigDecimal endorsementAmount;
	@JsonProperty("EndorsementCalcType")
	private String endorsementCalcType;
	@JsonProperty("EndorsementForId")
	private String endorsementforId;
	@JsonProperty("SubCoverId")
	public String subCoverId;
	@JsonProperty("MaxEndorsementAmount")
	public BigDecimal maxAmount;
	@JsonProperty("FactorTypeId")
	private String factorTypeId;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("PremiumBeforeDiscountLC")
	private BigDecimal premiumBeforeDiscountLC;
	@JsonProperty("PremiumAfterDiscountLC")
	private BigDecimal premiumAfterDiscountLC;
	@JsonProperty("premiumExcludedTaxLC")
	private BigDecimal premiumExcluedTaxLC;
	@JsonProperty("PremiumIncludedTaxLC")
	private BigDecimal premiumIncludedTaxLC;

	@JsonProperty("PremiumBeforeDiscount")
	private BigDecimal premiumBeforeDiscount;
	@JsonProperty("PremiumAfterDiscount")
	private BigDecimal premiumAfterDiscount;
	@JsonProperty("PremiumExcludedTax")
	private BigDecimal premiumExcluedTax;
	@JsonProperty("PremiumIncludedTax")
	private BigDecimal premiumIncludedTax;

	@JsonProperty("EndtCount")
	private BigDecimal endtCount;

	@JsonProperty("ProRata")
	private BigDecimal proRata;
	@JsonProperty("ProRataApplicable")
	private String proRataYn;
	@JsonProperty("Taxes")
	private List<Tax> taxes;

	@JsonProperty("EndorsementFees")
	private List<Tax> endtFees;
	///////////////////////////

	@JsonProperty("EndorsementCoverName")
	public String coverName;

	@JsonProperty("EndorsementMinimumPremium")
	public BigDecimal minimumPremium;
	@JsonProperty("EndorsementMinimumPremiumYn")
	private String minimumPremiumYn;

	@JsonProperty("IsSubCover")
	public String isSubCover;
	@JsonProperty("EndorsementSumInsured")
	public BigDecimal endorsementsumInsured;
	@JsonProperty("EndorsementSumInsuredLc")
	public BigDecimal endorsementsumInsuredLc;
	@JsonProperty("SubCoverDesc")
	public String subCoverDesc;
	@JsonProperty("SubCoverName")
	public String subCoverName;
	@JsonProperty("SectionId")
	private String sectionId;
	@JsonProperty("DependentCoverYN")
	private String dependentCoveryn;
	@JsonProperty("DependentCoverId")
	private String dependentCoverId;
	@JsonProperty("CoverageType")
	private String coverageType;
	@JsonProperty("isSelected")
	private String isselected;
	@JsonProperty("Notsutable")
	private boolean notsutable;
	@JsonProperty("ExchangeRate")
	private BigDecimal exchangeRate;
	@JsonProperty("Currency")
	private String currency;
	@JsonProperty("isReferal")
	private String isReferral;
	@JsonProperty("ReferalDescription")
	private String referalDescription;
	@JsonProperty("RegulatorSumInsured")
	private BigDecimal tiraSumInsured;
	@JsonProperty("RegulatorRate")
	private Double tiraRate;
	@JsonProperty("UserOpt")
	private String userOpt;
	@JsonProperty("CoverBasedOn")
	private String coverBasedOn;
	@JsonProperty("InsuranceId")
	private String insuranceId;
	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonProperty("AgencyCode")
	private String agencyCode;
	@JsonProperty("ProductId")
	private String productId;
	@JsonProperty("MSRefNo")
	private String msrefno;
	@JsonProperty("VehicleId")
	private String vehicleId;
	@JsonProperty("CdRefNo")
	private String cdRefNo;
	@JsonProperty("VdRefNo")
	private String vdRefNo;
	@JsonProperty("CreatedBy")
	private String createdBy;
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	@JsonProperty("MultiSelectYn")
	private String multiSelectYn;
	@JsonProperty("SectionName")
	private String sectionName;
	@JsonProperty("ExcessPercent")
	private BigDecimal excessPercent;
	@JsonProperty("ExcessAmount")
	private BigDecimal excessAmount;
	@JsonProperty("ExcessDesc")
	private String excessDesc;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("DiffPremiumIncludedTax")
	private BigDecimal diffPremiumIncludedTax;
	@JsonProperty("DiffPremiumIncludedTaxLC")
	private BigDecimal diffPremiumIncludedTaxLC;
	@JsonProperty("CoverageLimit")
	private BigDecimal coverageLimit;
	@JsonProperty("PolicyPeriod")
	private BigDecimal policyPeriod;

	@JsonProperty("Add")
	private String addcover;

	@JsonProperty("EndtOpdt")
	private String endtOpdt;

}
