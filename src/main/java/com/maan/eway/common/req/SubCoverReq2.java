package com.maan.eway.common.req;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Loading;

import lombok.Data;

@Data
public class SubCoverReq2 {

	@JsonProperty("LocationId")
	private String locationId;

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
	// only for ui
	@JsonProperty("ActualRate")
	public Double actualrate;
}
