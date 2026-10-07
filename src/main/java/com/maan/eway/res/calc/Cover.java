package com.maan.eway.res.calc;

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
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Cover implements Serializable {
	@JsonProperty("CoverId")
	public String coverId;
	@JsonProperty("CalcType")
	public String calcType;
	@JsonProperty("CoverName")
	public String coverName;
	@JsonProperty("CoverDesc")
	public String coverDesc;
	
	@JsonProperty("MinimumPremium")
	public BigDecimal minimumPremium;
	@JsonProperty("CoverToolTip")
	public String coverToolTip;
	@JsonProperty("IsSubCover")
	public String isSubCover;
	@JsonProperty("SumInsured")
	public BigDecimal sumInsured;
	@JsonProperty("SumInsuredLc")
	public BigDecimal sumInsuredLc;
	@JsonProperty("Rate")
	public Double rate;
	@JsonProperty("SubCoverId")
	public String subCoverId;
	@JsonProperty("SubCoverDesc")
	public String subCoverDesc;
	@JsonProperty("SubCoverName")
	public String subCoverName;
	@JsonProperty("SectionId")
	private String sectionId;
	@JsonProperty("Discounts")
	public List<Discount> discounts;
	@JsonProperty("Taxes")
	public List<Tax> taxes;

	@JsonProperty("SubCovers")
	public List<Cover> subcovers;

	@JsonProperty("FactorTypeId")
	private String factorTypeId;

	@JsonProperty("DependentCoverYN")
	private String dependentCoveryn;

	@JsonProperty("DependentCoverId")
	private String dependentCoverId;

	@JsonProperty("Exception")
	private CoverException error;

	@JsonProperty("Loadings")
	public List<Loading> loadings;
	@JsonProperty("CoverageType")
	private String coverageType;
	@JsonProperty("isSelected")
	private String isselected;

	@JsonProperty("Notsutable")
	private boolean notsutable;

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

	@JsonProperty("ExchangeRate")
	private BigDecimal exchangeRate;
	@JsonProperty("Currency")
	private String currency;

	@JsonProperty("isReferal")
	private String isReferral;

	@JsonProperty("ReferalDescription")
	private String referalDescription;
	@JsonProperty("ProRata")
	private BigDecimal proRata;

	@JsonProperty("RegulatorSumInsured")
	private BigDecimal tiraSumInsured;
	@JsonProperty("RegulatorRate")
	private Double tiraRate;

	@JsonProperty("UserOpt")
	private String userOpt;

	@JsonProperty("CoverBasedOn")
	private String coverBasedOn;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

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

	@JsonProperty("MinimumPremiumYn")
	private String minimumPremiumYn;
	@JsonProperty("ProRataApplicable")
	private String proRataYn;

	@JsonProperty("Endorsements")
	private List<Endorsement> endorsements;

	@JsonProperty("EndtCount")
	private BigDecimal endtCount;

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

	@JsonProperty("MinSumInsured")
	private BigDecimal minSumInsured;

	@JsonProperty("PolicyPeriod")
	private BigDecimal policyPeriod;

	@JsonProperty("IsTaxExcempted")
	private String isTaxExcempted;
	@JsonProperty("FreeCoverLimit")
	private BigDecimal freeCoverLimit;

	@JsonProperty("CoverNameLocal")
	public String coverNameLocal;
	@JsonProperty("CoverDescLocal")
	public String coverDescLocal;
	@JsonProperty("SubCoverDescLocal")
	public String subCoverDescLocal;
	@JsonProperty("SubCoverNameLocal")
	public String subCoverNameLocal;
	@JsonProperty("LocationId")
	public String locationId;
	@JsonProperty("MinRate")
	public Double minrate;
	@JsonProperty("MinimumRateYn")
	private String minimumRateYn;
	@JsonProperty("MaxRate")
	public Double maxrate;
	@JsonProperty("MaximumRateYn")
	private String maximumRateYn;
	// only for ui
	@JsonProperty("ActualRate")
	public Double actualrate;


	@JsonProperty("ContentDesc")
	private String contentDesc;
	
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
    
    @JsonProperty("DependentCoveSIorPI")
	private String dependentCoveSIorPI;
    
    @JsonProperty("EndtProRataYn")
	private String endtProRataYn;
    
    @JsonProperty("EndtProRataDesc")
	private String endtProRataDesc;
    
    @JsonProperty("CoverAgetype")
	private String coverAgetype;
    
    
    
	

}
