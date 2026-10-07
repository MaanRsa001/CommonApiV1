package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverIdsReq {


	@JsonProperty("CoverId")
	private Integer coverId;
	
	@JsonProperty("SubCoverYn")
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
	
}
