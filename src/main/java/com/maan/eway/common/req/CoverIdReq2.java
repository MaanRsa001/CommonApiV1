package com.maan.eway.common.req;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Loading;

import lombok.Data;

@Data
public class CoverIdReq2 {

	@JsonProperty("LocationId")
	private String locationId;
	
	@JsonProperty("Currency")
	private String currency;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;
	
	@JsonProperty("SectionId")
	private String sectionId;

	@JsonProperty("CoverId")
	private Integer coverId;

	@JsonProperty("IsSubCover")
	private String subCoverYn;

	@JsonProperty("SubCoverId")
	private String subCoverId;

	@JsonProperty("isReferal")
	private String isReferal;

	@JsonProperty("MinimumPremium")
	private String minimumPremium;

	@JsonProperty("Rate")
	private String rate;

	@JsonProperty("UserOpt")
	private String userOpt;

	@JsonProperty("CoverageType")
	private String coverageType;

	@JsonProperty("ExcessAmount")
	private String excessAmount;

	@JsonProperty("ExcessPercent")
	private String excessPercent;

	@JsonProperty("ExcessDesc")
	private String excessDesc;

	@JsonProperty("Discounts")
	public List<Discount> discounts;

	@JsonProperty("Loadings")
	public List<Loading> loadings;

	@JsonProperty("Endorsements")
	private List<Endorsement> endorsements;

	@JsonProperty("EndtCount")
	private BigDecimal endtCount;
	@JsonProperty("MinRate")
	public Double minrate;
	
	@JsonProperty("MaxRate")
	public Double maxrate;
	// only for ui
	@JsonProperty("ActualRate")
	public Double actualrate;
	
	@JsonProperty("SubCovers")
	private List<SubCoverReq2> subCoversList;
	
	
	@JsonProperty("CoverageLimit")
	private String  coverageLimit;

	
	@JsonProperty("SumInsured")
	private String  sumInsured;
	
	@JsonProperty("SumInsuredLc")
	private String  SumInsuredLc;
	
	@JsonProperty("RiskId")
	private String  riskId;
	
	

}
